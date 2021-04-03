package activejdbc.pojo.builder.annotation.processor;

import activejdbc.pojo.builder.annotation.ActiveJdbcRequiredProperties;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("activejdbc.pojo.builder.annotation.ActiveJdbcRequiredProperties")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ActiveJdbcRequiredPropertyProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elementsAnnotatedWith) {
                if (element.getKind() != ElementKind.CLASS) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Only classes can be annotated with ActiveJdbcRequiredProperty",
                            element);
                }
                List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
                annotationMirrors.stream().findFirst().ifPresent(annotationMirror -> {
                    if (annotationMirror.getAnnotationType().toString().equals(ActiveJdbcRequiredProperties.class.getName())) {
                        String packageName = element.getEnclosingElement().toString();
                        String className = element.getSimpleName().toString();
                        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
                        // todo find a solution to extract one value
                        Collection<? extends AnnotationValue> values = elementValues.values();
                        values.forEach(annotationValue -> {
                            List<AnnotationMirror> internalAnnotationMirrors = (List<AnnotationMirror>) annotationValue.getValue();
                            String wrapperClassBody = ActiveJdbcRequiredPropertyWrapperFactory.build(packageName, className, internalAnnotationMirrors);
                            try {
                                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className + "Wrapper");
                                try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
                                    out.print(wrapperClassBody);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }

        }
        return false;
    }
}
