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
import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import activejdbc.wrapper.annotation.processor.util.AnnotationValueExtractor;
import activejdbc.wrapper.annotation.processor.util.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty", "activejdbc.wrapper.annotation.ActiveJdbcRequiredProperties"})
@SupportedOptions({ActiveJdbcRequiredPropertyProcessor.ACTIVEJDBC_WRAPPER_SUFFIX,ActiveJdbcRequiredPropertyProcessor.ACTIVEJDBC_BUILDER_METHOD_PREFIX})
public class ActiveJdbcRequiredPropertyProcessor extends AbstractProcessor {

    private static final String DEFAULT_WRAPPER_SUFFIX = "Wrapper";
    public static final String ACTIVEJDBC_WRAPPER_SUFFIX = "activejdbc.wrapper.suffix";
    public static final String ACTIVEJDBC_BUILDER_METHOD_PREFIX = "activejdbc.wrapper.builder.method.prefix";
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
                List<? extends AnnotationMirror> annotationMirrors = filterNeededAnnotationMirrors(element.getAnnotationMirrors());
                List<ColumnContext> columnContexts = wrapperFactory.getColumnContexts(annotationMirrors);
                if (!hasValidColumns(columnContexts, element)) {
                    continue;
                }
                String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
                String className = element.getSimpleName().toString();
                String wrapperClassName = className + wrapperSuffix;
                String qualifiedWrapperClassName = packageName.isEmpty() ? wrapperClassName : packageName + "." + wrapperClassName;

                String wrapperClassBody = wrapperFactory.build(packageName, className, columnContexts);
                try {
                    JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(qualifiedWrapperClassName, element);
                    try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
                        out.print(wrapperClassBody);
                    }
                } catch (IOException e) {
                    error(String.format("Unable to create %s: %s", qualifiedWrapperClassName, e.getMessage()), element);
                }
            }

        }
        return false;
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
        AnnotationProcessorContext annotationProcessorContext = new AnnotationProcessorContext(suffix, builderMethodPrefix);
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

    private void error(String message, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
