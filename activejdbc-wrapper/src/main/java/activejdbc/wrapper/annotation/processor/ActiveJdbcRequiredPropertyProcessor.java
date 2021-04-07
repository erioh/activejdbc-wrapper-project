package activejdbc.wrapper.annotation.processor;

import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperties;
import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;
import activejdbc.wrapper.annotation.processor.util.AnnotationValueExtractor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty", "activejdbc.wrapper.annotation.ActiveJdbcRequiredProperties"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ActiveJdbcRequiredPropertyProcessor extends AbstractProcessor {

    public static final String WRAPPER_SUFFIX = "Wrapper";
    private final ActiveJdbcRequiredPropertyWrapperFactory wrapperFactory = new ActiveJdbcRequiredPropertyWrapperFactory(WRAPPER_SUFFIX);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elementsAnnotatedWith) {
                if (element.getKind() != ElementKind.CLASS) {
                    throw new IllegalArgumentException("Only classes can be annotated with ActiveJdbcRequiredProperty");
                }
                List<? extends AnnotationMirror> annotationMirrors = filterNeededAnnotationMirrors(element.getAnnotationMirrors());
                String packageName = element.getEnclosingElement().toString();
                String className = element.getSimpleName().toString();

                String wrapperClassBody = wrapperFactory.build(packageName, className, annotationMirrors);
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
}
