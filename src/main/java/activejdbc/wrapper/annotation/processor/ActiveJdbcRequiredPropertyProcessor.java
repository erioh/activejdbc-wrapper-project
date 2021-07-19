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
import activejdbc.wrapper.annotation.processor.util.AnnotationValueExtractor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty", "activejdbc.wrapper.annotation.ActiveJdbcRequiredProperties"})
public class ActiveJdbcRequiredPropertyProcessor extends AbstractProcessor {

    public static final String WRAPPER_SUFFIX = "Wrapper";
    private ActiveJdbcRequiredPropertyWrapperFactory wrapperFactory;
    private AnnotationProcessorContext annotationProcessorContext;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elementsAnnotatedWith) {
                if (element.getKind() != ElementKind.CLASS) {
                    throw new IllegalArgumentException("Only classes can be annotated with ActiveJdbcRequiredProperty");
                }
                List<? extends AnnotationMirror> annotationMirrors = filterNeededAnnotationMirrors(element.getAnnotationMirrors());
                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
                String className = element.getSimpleName().toString();

                String wrapperClassBody = wrapperFactory.build(packageElement.getQualifiedName().toString(), className, annotationMirrors);
                try {
                    JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className + WRAPPER_SUFFIX);
                    try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
                        out.print(wrapperClassBody);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
                .map(annotationValue -> (List<AnnotationMirror>) annotationValue.getValue())
                .findFirst()
                .orElseGet(() -> annotationMirrors.stream()
                        .filter(annotationMirror -> annotationMirror.getAnnotationType().toString().equals(ActiveJdbcRequiredProperty.class.getName()))
                        .collect(Collectors.toList()));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.annotationProcessorContext = new AnnotationProcessorContext(WRAPPER_SUFFIX);
        this.wrapperFactory = annotationProcessorContext.init();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
