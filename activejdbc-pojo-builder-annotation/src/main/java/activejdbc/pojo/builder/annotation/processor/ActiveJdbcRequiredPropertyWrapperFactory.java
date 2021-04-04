package activejdbc.pojo.builder.annotation.processor;

import activejdbc.pojo.builder.annotation.processor.builder.WrapperClassBuilder;
import activejdbc.pojo.builder.annotation.processor.util.AnnotationValueExtractor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.List;
import java.util.function.BiConsumer;

public class ActiveJdbcRequiredPropertyWrapperFactory {
    private final String wrapperSuffix;

    public ActiveJdbcRequiredPropertyWrapperFactory(String wrapperSuffix) {
        this.wrapperSuffix = wrapperSuffix;
    }

    public String build(String packageName, String className, List<? extends AnnotationMirror> annotationMirrors) {
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, className, wrapperSuffix);
        // add setters
        addMethod(annotationMirrors, wrapperClassBuilder::withSetter);
        // add getters
        addMethod(annotationMirrors, wrapperClassBuilder::withGetter);
        // add get activejdbc object method
        wrapperClassBuilder.withMethodGetActivejdbcObject();
        // add set activejdbc object method
        wrapperClassBuilder.withMethodSetActivejdbcObject();
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
