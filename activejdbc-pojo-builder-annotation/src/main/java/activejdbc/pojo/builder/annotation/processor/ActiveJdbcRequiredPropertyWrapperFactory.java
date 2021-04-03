package activejdbc.pojo.builder.annotation.processor;

import activejdbc.pojo.builder.annotation.processor.util.AnnotationValueExtractor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static activejdbc.pojo.builder.annotation.processor.util.StringTemplates.*;
import static activejdbc.pojo.builder.annotation.processor.util.StringUtils.*;

public class ActiveJdbcRequiredPropertyWrapperFactory {
    private final String wrapperSuffix;
    private final Map<String, String> propertyNamesAndGetters = new HashMap<>();

    public ActiveJdbcRequiredPropertyWrapperFactory(String wrapperSuffix) {
        this.wrapperSuffix = wrapperSuffix;
    }

    public String build(String packageName, String className, List<? extends AnnotationMirror> annotationMirrors) {
        String wrappersClassName = className + wrapperSuffix;
        String activeJdbcObjectName = lowerCaseFirstCharacter(className);
        StringBuilder methodsContainer = new StringBuilder();
        // add setters
        generateSetters(methodsContainer, annotationMirrors, activeJdbcObjectName);
        // add getters
        generateGetters(methodsContainer, annotationMirrors, activeJdbcObjectName);
        // add from method
        generateGetActivejdbcObjectMethod(methodsContainer, className, activeJdbcObjectName);
        // add put method
        generateSetActivejdbcObjectMethod(methodsContainer, className, activeJdbcObjectName);
        // add toString (using getters)
        generateToStringMethod(methodsContainer);
        // add equals
        generateEqualsMethod(methodsContainer, wrappersClassName);
        // and hashcode (using getters)
        generateHashCode(methodsContainer);
        return buildClass(
                packageName,
                className,
                wrappersClassName,
                activeJdbcObjectName,
                methodsContainer.toString()
        );
    }

    private void generateHashCode(StringBuilder stringBuilder) {
        stringBuilder.append(buildHashCode(propertyNamesAndGetters.values()));
    }

    private void generateEqualsMethod(StringBuilder stringBuilder, String wrappersClassName) {
        stringBuilder.append(buildEquals(wrappersClassName, propertyNamesAndGetters.values()));
    }

    private void generateToStringMethod(StringBuilder stringBuilder) {
        stringBuilder.append(buildToString(propertyNamesAndGetters));
    }

    private void generateSetActivejdbcObjectMethod(StringBuilder stringBuilder, String className, String activeJdbcObjectName) {
        stringBuilder.append(buildMethodSetObject(className, activeJdbcObjectName));
    }

    private void generateGetActivejdbcObjectMethod(StringBuilder stringBuilder, String className, String activeJdbcObjectName) {
        stringBuilder.append(buildMethodGetObject(className, activeJdbcObjectName));
    }

    private void generateGetters(StringBuilder stringBuilder, List<? extends AnnotationMirror> annotationMirrors, String activeJdbcObjectName) {
        annotationMirrors.forEach(annotationMirror -> {
            AnnotationValue clazz = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz");
            AnnotationValue field = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "field");
            String columnName = field.getValue().toString();
            String propertyName = buildPropertyName(field.getValue().toString());
            String getterName = buildMethodName(columnName, "get");
            propertyNamesAndGetters.put(propertyName, getterName);
            stringBuilder.append(buildGetter(getterName, clazz.getValue().toString(), activeJdbcObjectName, columnName));
        });
    }

    private void generateSetters(StringBuilder stringBuilder, List<? extends AnnotationMirror> annotationMirrors, String activeJdbcObjectName) {
        annotationMirrors.forEach(
                annotationMirror -> {
                    AnnotationValue clazz = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz");
                    AnnotationValue field = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "field");
                    String settersName = buildMethodName(field.getValue().toString(), "set");
                    String propertyName = buildPropertyName(field.getValue().toString());
                    String columnName = field.getValue().toString();
                    stringBuilder.append(buildSetter(settersName, clazz.getValue().toString(), propertyName, activeJdbcObjectName, columnName));
                });
    }
}
