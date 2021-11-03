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
import activejdbc.wrapper.annotation.processor.exception.AnnotationProcessorException;
import activejdbc.wrapper.annotation.processor.util.AnnotationValueExtractor;
import activejdbc.wrapper.annotation.processor.util.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty", "activejdbc.wrapper.annotation.ActiveJdbcRequiredProperties"})
@SupportedOptions({ActiveJdbcRequiredPropertyProcessor.ACTIVEJDBC_WRAPPER_SUFFIX,ActiveJdbcRequiredPropertyProcessor.ACTIVEJDBC_BUILDER_METHOD_PREFIX})
public class ActiveJdbcRequiredPropertyProcessor extends AbstractProcessor {

    private static final String DEFAULT_WRAPPER_SUFFIX = "Wrapper";
    public static final String ACTIVEJDBC_WRAPPER_SUFFIX = "activejdbc.wrapper.suffix";
    public static final String ACTIVEJDBC_BUILDER_METHOD_PREFIX = "activejdbc.wrapper.builder.method.prefix";
    private ActiveJdbcRequiredPropertyWrapperFactory wrapperFactory;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elementsAnnotatedWith) {
                if (element.getKind() != ElementKind.CLASS) {
                    throw new IllegalArgumentException("Only classes can be annotated with ActiveJdbcRequiredProperty");
                }
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found @ActiveJdbcRequiredProperty at " + element);
                List<? extends AnnotationMirror> annotationMirrors = filterNeededAnnotationMirrors(element.getAnnotationMirrors());
                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
                String className = element.getSimpleName().toString();

                String wrapperClassBody = wrapperFactory.build(packageElement.getQualifiedName().toString(), className, annotationMirrors);
                try {
                    JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className + getWrapperSuffix());
                    try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
                        out.print(wrapperClassBody);
                    }
                } catch (IOException e) {
                    throw new AnnotationProcessorException(e);
                }
            }

        }
        return false;
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
        AnnotationProcessorContext annotationProcessorContext = new AnnotationProcessorContext(getWrapperSuffix(), getBuilderMethodPrefix());
        this.wrapperFactory = annotationProcessorContext.init();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private String getWrapperSuffix() {
        String suffix = this.processingEnv.getOptions().get(ACTIVEJDBC_WRAPPER_SUFFIX);
        if (StringUtils.isBlank(suffix)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("Custom suffix is not set. Suffix [%s] will be used instead.\r\n", DEFAULT_WRAPPER_SUFFIX));
            return DEFAULT_WRAPPER_SUFFIX;
        }

        if (!StringUtils.isValid(suffix)) {
            throw new IllegalArgumentException("Custom suffix has illegal characters. Please use only characters in uppercase and lowercase, underscore, and numbers.");
        }
        return suffix;
    }

    private String getBuilderMethodPrefix() {
        String prefix = this.processingEnv.getOptions().get(ACTIVEJDBC_BUILDER_METHOD_PREFIX);
        if (StringUtils.isBlank(prefix)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Custom builder method prefix is not set. \r\n");
            return StringUtils.EMPTY_STRING;
        }

        if (!StringUtils.isValid(prefix)) {
            throw new IllegalArgumentException("Custom prefix has illegal characters. Please use only characters in uppercase and lowercase, underscore, and numbers.");
        }
        return prefix;
    }
}
