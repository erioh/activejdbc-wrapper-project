package activejdbc.wrapper.annotation.processor;

import activejdbc.wrapper.annotation.processor.builder.WrapperClassBuilder;
import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.util.AnnotationValueExtractor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.List;
import java.util.function.BiConsumer;

public class ActiveJdbcRequiredPropertyWrapperFactory {
    private final AnnotationProcessorContext annotationProcessorContext;

    public ActiveJdbcRequiredPropertyWrapperFactory(AnnotationProcessorContext annotationProcessorContext) {
        this.annotationProcessorContext = annotationProcessorContext;
    }

    public String build(String packageName, String className, List<? extends AnnotationMirror> annotationMirrors) {
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, className, annotationProcessorContext);
        // add setters
        addMethod(annotationMirrors, wrapperClassBuilder::withSetter);
        // add getters
        addMethod(annotationMirrors, wrapperClassBuilder::withGetter);
        // add get activejdbc object method
        wrapperClassBuilder.withMethodGetActivejdbcObject();
        // add toString (using getters)
        wrapperClassBuilder.withToString();
        // add equals
        wrapperClassBuilder.withEquals();
        // and hashcode (using getters)
        wrapperClassBuilder.withHashCode();
        return wrapperClassBuilder.buildClassBody();
    }

    private void addMethod(List<? extends AnnotationMirror> annotationMirrors, BiConsumer<String, String> classBuilderConsumer) {
        annotationMirrors.forEach(annotationMirror -> {
            AnnotationValue clazz = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz");
            AnnotationValue field = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "field");
            String columnName = field.getValue().toString();
            classBuilderConsumer.accept(clazz.getValue().toString(), columnName);
        });
    }
}
