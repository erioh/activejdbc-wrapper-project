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
    private static final String NEW_LINE = System.getProperty("line.separator");
    private final Map<String, String> propertyNamesAndGetters = new HashMap<>();

    public String build(String packageName, String className, List<AnnotationMirror> annotationMirrors) {
        String wrappersClassName = className + "Wrapper";
        // adding package
        StringBuilder stringBuilder = new StringBuilder()
                .append("package ").append(packageName).append(';').append(NEW_LINE);
        // adding import of activejbdc entity
        stringBuilder.append("import ").append(packageName).append('.').append(className).append(';').append(NEW_LINE);
        // creating class name
        stringBuilder.append("public class ").append(wrappersClassName).append(" {").append(NEW_LINE);
        // create private instance of activejdbc class
        String activeJdbcObjectName = lowerCaseFirstCharacter(className);
        stringBuilder.append("private ").append(className).append(" ")
                .append(activeJdbcObjectName)
                .append(" = new ")
                .append(className)
                .append("();")
                .append(NEW_LINE);
        // add setters
        generateSetters(stringBuilder, annotationMirrors, activeJdbcObjectName);
        // add getters
        generateGetters(stringBuilder, annotationMirrors, activeJdbcObjectName);
        // add from method
        generateGetActivejdbcObjectMethod(stringBuilder, className, activeJdbcObjectName);
        // add put method
        generateSetActivejdbcObjectMethod(stringBuilder, className, activeJdbcObjectName);
        // add toString (using getters)
        generateToString(stringBuilder);
        // add equals and hashcode (using getters)
        // close the class
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    private void generateToString(StringBuilder stringBuilder) {
        stringBuilder.append(buildToString(propertyNamesAndGetters));
    }

    private void generateSetActivejdbcObjectMethod(StringBuilder stringBuilder, String className, String activeJdbcObjectName) {
        stringBuilder.append(buildMethodSetObject(className, activeJdbcObjectName));
    }

    private void generateGetActivejdbcObjectMethod(StringBuilder stringBuilder, String className, String activeJdbcObjectName) {
        stringBuilder.append(buildMethodGetObject(className, activeJdbcObjectName));
    }

    private void generateGetters(StringBuilder stringBuilder, List<AnnotationMirror> annotationMirrors, String activeJdbcObjectName) {
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

    private void generateSetters(StringBuilder stringBuilder, List<AnnotationMirror> annotationMirrors, String activeJdbcObjectName) {
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
