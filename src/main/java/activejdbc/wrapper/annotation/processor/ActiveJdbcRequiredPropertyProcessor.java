/*
Copyright 2021-2021 Serhii Demenkov
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package activejdbc.wrapper.annotation.processor;

import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperties;
import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;
import activejdbc.wrapper.annotation.processor.adapter.builder.WrapperClassBuilder;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.WrapperQueryClassBuilder;
import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import activejdbc.wrapper.annotation.processor.util.AnnotationValueExtractor;
import activejdbc.wrapper.annotation.processor.util.SourceFormatter;
import activejdbc.wrapper.annotation.processor.util.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty", "activejdbc.wrapper.annotation.ActiveJdbcRequiredProperties"})
@SupportedOptions({ActiveJdbcRequiredPropertyProcessor.ACTIVEJDBC_WRAPPER_SUFFIX,ActiveJdbcRequiredPropertyProcessor.ACTIVEJDBC_BUILDER_METHOD_PREFIX})
public class ActiveJdbcRequiredPropertyProcessor extends AbstractProcessor {

    private static final String DEFAULT_WRAPPER_SUFFIX = "Wrapper";
    public static final String ACTIVEJDBC_WRAPPER_SUFFIX = "activejdbc.wrapper.suffix";
    public static final String ACTIVEJDBC_BUILDER_METHOD_PREFIX = "activejdbc.wrapper.builder.method.prefix";
    private static final String MODEL_CLASS = "org.javalite.activejdbc.Model";
    private static final String ILLEGAL_CHARACTERS_MESSAGE = "%s has illegal characters. Please use only characters in uppercase and lowercase, underscore, and numbers.";
    // generated getters return null for NULL columns and toString compares with null, so primitives can't be used
    private static final Set<String> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList("boolean", "byte", "char", "short", "int", "long", "float", "double"));
    private String wrapperSuffix;
    // stays null when options are invalid, nothing is generated in that case
    private ActiveJdbcRequiredPropertyWrapperFactory wrapperFactory;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (wrapperFactory == null) {
            return false;
        }
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elementsAnnotatedWith) {
                if (element.getKind() != ElementKind.CLASS) {
                    error("Only classes can be annotated with ActiveJdbcRequiredProperty", element);
                    continue;
                }
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found @ActiveJdbcRequiredProperty at " + element);
                if (!isReachableFromWrapper((TypeElement) element) || !isInstantiableModel((TypeElement) element)) {
                    continue;
                }
                List<? extends AnnotationMirror> annotationMirrors = filterNeededAnnotationMirrors(element.getAnnotationMirrors());
                List<ColumnContext> columnContexts = withConverters(annotationMirrors, wrapperFactory.getColumnContexts(annotationMirrors), (TypeElement) element);
                if (columnContexts == null || !hasValidColumns(columnContexts, element)) {
                    continue;
                }
                for (ColumnContext leftOut : WrapperQueryClassBuilder.columnsLeftOut(columnContexts)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format(
                            "Column [%s] is not available in the query builder: property name [%s] clashes with a method of the query class. Set desiredFieldName to use it in queries.",
                            leftOut.getColumnName(), leftOut.getPropertyName()), element);
                }
                String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
                String qualifiedName = ((TypeElement) element).getQualifiedName().toString();
                // Outer.Inner for nested classes
                String className = packageName.isEmpty() ? qualifiedName : qualifiedName.substring(packageName.length() + 1);
                String wrapperClassName = WrapperClassBuilder.wrapperClassName(className, wrapperSuffix);
                String qualifiedWrapperClassName = packageName.isEmpty() ? wrapperClassName : packageName + "." + wrapperClassName;

                String wrapperClassBody = wrapperFactory.build(packageName, className, columnContexts);
                try {
                    JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(qualifiedWrapperClassName, element);
                    try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
                        out.print(SourceFormatter.format(wrapperClassBody));
                    }
                } catch (IOException e) {
                    error(String.format("Unable to create %s: %s", qualifiedWrapperClassName, e.getMessage()), element);
                }
            }

        }
        return false;
    }

    /**
     * The wrapper is a top-level class in the same package, so it can only use top-level and static nested classes
     * that aren't private. Local classes never get here: javac doesn't pass their annotations to processors.
     */
    private boolean isReachableFromWrapper(TypeElement element) {
        for (Element current = element; current.getKind() != ElementKind.PACKAGE; current = current.getEnclosingElement()) {
            Element enclosing = current.getEnclosingElement();
            if (enclosing.getKind() == ElementKind.PACKAGE) {
                return true;
            }
            if (!current.getModifiers().contains(Modifier.STATIC)) {
                error(String.format("Class [%s] is an inner class, make it static to be annotated with ActiveJdbcRequiredProperty.", ((TypeElement) current).getQualifiedName()), element);
                return false;
            }
            if (current.getModifiers().contains(Modifier.PRIVATE)) {
                error(String.format("Class [%s] is private, the generated wrapper can't access it.", ((TypeElement) current).getQualifiedName()), element);
                return false;
            }
        }
        return true;
    }

    /**
     * The wrapper extends ActivejdbcWrapper&lt;T extends Model&gt; and creates the model with new T().
     */
    private boolean isInstantiableModel(TypeElement element) {
        TypeElement model = processingEnv.getElementUtils().getTypeElement(MODEL_CLASS);
        TypeMirror erasedType = processingEnv.getTypeUtils().erasure(element.asType());
        if (model == null || !processingEnv.getTypeUtils().isSubtype(erasedType, model.asType())) {
            error(String.format("Class [%s] must extend %s to be annotated with ActiveJdbcRequiredProperty.", element.getQualifiedName(), MODEL_CLASS), element);
            return false;
        }
        if (element.getModifiers().contains(Modifier.ABSTRACT)) {
            error(String.format("Class [%s] is abstract, but the wrapper needs to create its instances.", element.getQualifiedName()), element);
            return false;
        }
        boolean hasAccessibleNoArgConstructor = ElementFilter.constructorsIn(element.getEnclosedElements()).stream()
                .anyMatch(constructor -> constructor.getParameters().isEmpty() && !constructor.getModifiers().contains(Modifier.PRIVATE));
        if (!hasAccessibleNoArgConstructor) {
            error(String.format("Class [%s] needs a non-private constructor without parameters, the wrapper uses it to create instances.", element.getQualifiedName()), element);
            return false;
        }
        return true;
    }

    /**
     * @return the columns with their converters, or null if a converter can't be used (errors are reported)
     */
    private List<ColumnContext> withConverters(List<? extends AnnotationMirror> annotationMirrors, List<ColumnContext> columnContexts, TypeElement element) {
        ConverterResolver converterResolver = new ConverterResolver(processingEnv);
        List<ColumnContext> resolved = new ArrayList<>(columnContexts.size());
        boolean valid = true;
        for (int i = 0; i < columnContexts.size(); i++) {
            ColumnContext columnContext = converterResolver.resolve(annotationMirrors.get(i), columnContexts.get(i), element);
            valid &= columnContext != null;
            resolved.add(columnContext);
        }
        return valid ? resolved : null;
    }

    private boolean hasValidColumns(List<ColumnContext> columnContexts, Element element) {
        boolean valid = true;
        for (ColumnContext columnContext : columnContexts) {
            if (PRIMITIVE_TYPES.contains(columnContext.getClazz())) {
                valid = false;
                error(String.format("Column [%s] has primitive type [%s], which is not supported. Please use its wrapper class instead.",
                        columnContext.getColumnName(), columnContext.getClazz()), element);
            }
            String propertyName = columnContext.getPropertyName();
            if (SourceVersion.isIdentifier(propertyName) && !SourceVersion.isKeyword(propertyName)) {
                continue;
            }
            valid = false;
            if (StringUtils.isBlank(columnContext.getDesiredFieldName())) {
                error(String.format("Column [%s] produces property name [%s], which is not a valid Java identifier. Please set desiredFieldName for this column.",
                        columnContext.getColumnName(), propertyName), element);
            } else {
                error(String.format("desiredFieldName [%s] of column [%s] is not a valid Java identifier.",
                        propertyName, columnContext.getColumnName()), element);
            }
        }
        return valid && hasUniquePropertyNames(columnContexts, element);
    }

    private boolean hasUniquePropertyNames(List<ColumnContext> columnContexts, Element element) {
        boolean valid = true;
        // keyed by getter name: "address" and "Address" are different fields but the same getAddress()
        Map<String, String> columnNamesByGetterName = new HashMap<>();
        for (ColumnContext columnContext : columnContexts) {
            String propertyName = columnContext.getPropertyName();
            String previousColumnName = columnNamesByGetterName.putIfAbsent(StringUtils.buildMethodName(propertyName, "get"), columnContext.getColumnName());
            if (previousColumnName == null) {
                continue;
            }
            valid = false;
            if (previousColumnName.equals(columnContext.getColumnName())) {
                error(String.format("Column [%s] is declared more than once.", previousColumnName), element);
            } else {
                error(String.format("Columns [%s] and [%s] both produce property name [%s]. Please set a different desiredFieldName for one of them.",
                        previousColumnName, columnContext.getColumnName(), propertyName), element);
            }
        }
        return valid && hasUniqueConstantNames(columnContexts, element);
    }

    private boolean hasUniqueConstantNames(List<ColumnContext> columnContexts, Element element) {
        boolean valid = true;
        Map<String, String> columnNamesByConstantName = new HashMap<>();
        for (ColumnContext columnContext : columnContexts) {
            String constantName = StringUtils.buildConstantName(columnContext.getColumnName());
            String previousColumnName = columnNamesByConstantName.putIfAbsent(constantName, columnContext.getColumnName());
            if (previousColumnName != null) {
                valid = false;
                error(String.format("Columns [%s] and [%s] both produce constant name [%s] in the wrapper.",
                        previousColumnName, columnContext.getColumnName(), constantName), element);
            }
        }
        return valid;
    }

    private List<? extends AnnotationMirror> filterNeededAnnotationMirrors(List<? extends AnnotationMirror> annotationMirrors) {
        return annotationMirrors.stream()
                .filter(annotationMirror -> annotationMirror.getAnnotationType().toString().equals(ActiveJdbcRequiredProperties.class.getName()))
                .map(AnnotationMirror::getElementValues)
                .map(elementValues -> AnnotationValueExtractor.extract(elementValues, "value"))
                .map(annotationValue -> (List<AnnotationMirror>) annotationValue.get().getValue())
                .findFirst()
                .orElseGet(() -> annotationMirrors.stream()
                        .filter(annotationMirror -> annotationMirror.getAnnotationType().toString().equals(ActiveJdbcRequiredProperty.class.getName()))
                        .collect(Collectors.toList()));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        String suffix = getWrapperSuffix();
        String builderMethodPrefix = getBuilderMethodPrefix();
        if (suffix == null || builderMethodPrefix == null) {
            return;
        }
        this.wrapperSuffix = suffix;
        AnnotationProcessorContext annotationProcessorContext = new AnnotationProcessorContext(suffix, builderMethodPrefix, getGeneratedAnnotation());
        this.wrapperFactory = annotationProcessorContext.init();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * @return suffix, or null if it is invalid (the error is already reported)
     */
    private String getWrapperSuffix() {
        String suffix = this.processingEnv.getOptions().get(ACTIVEJDBC_WRAPPER_SUFFIX);
        if (StringUtils.isBlank(suffix)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("Custom suffix is not set. Suffix [%s] will be used instead.\r\n", DEFAULT_WRAPPER_SUFFIX));
            return DEFAULT_WRAPPER_SUFFIX;
        }

        if (!StringUtils.isValid(suffix)) {
            error(String.format(ILLEGAL_CHARACTERS_MESSAGE, "Custom suffix"), null);
            return null;
        }
        return suffix;
    }

    /**
     * @return prefix, or null if it is invalid (the error is already reported)
     */
    private String getBuilderMethodPrefix() {
        String prefix = this.processingEnv.getOptions().get(ACTIVEJDBC_BUILDER_METHOD_PREFIX);
        if (StringUtils.isBlank(prefix)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Custom builder method prefix is not set. \r\n");
            return StringUtils.EMPTY_STRING;
        }

        if (!StringUtils.isValid(prefix)) {
            error(String.format(ILLEGAL_CHARACTERS_MESSAGE, "Custom prefix"), null);
            return null;
        }
        return prefix;
    }

    /**
     * javax.annotation.processing.Generated exists since Java 9, javax.annotation.Generated only up to Java 10
     * (and not with --release 9+), so pick whichever the code being compiled can see.
     */
    private String getGeneratedAnnotation() {
        for (String annotation : new String[]{"javax.annotation.processing.Generated", "javax.annotation.Generated"}) {
            if (processingEnv.getElementUtils().getTypeElement(annotation) != null) {
                return String.format("@%s(\"%s\")", annotation, ActiveJdbcRequiredPropertyProcessor.class.getName());
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    private void error(String message, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
