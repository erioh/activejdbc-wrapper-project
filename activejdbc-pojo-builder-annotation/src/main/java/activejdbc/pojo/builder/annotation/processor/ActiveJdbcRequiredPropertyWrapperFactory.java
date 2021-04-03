package activejdbc.pojo.builder.annotation.processor;

import activejdbc.pojo.builder.annotation.processor.util.AnnotationValueExtractor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.List;

import static activejdbc.pojo.builder.annotation.processor.util.StringTemplates.*;
import static activejdbc.pojo.builder.annotation.processor.util.StringUtils.*;

public class ActiveJdbcRequiredPropertyWrapperFactory {
    private static final String NEW_LINE = System.getProperty("line.separator");

    public static String build(String packageName, String className, List<AnnotationMirror> annotationMirrors) {

        // adding package
        StringBuilder stringBuilder = new StringBuilder()
                .append("package ").append(packageName).append(';').append(NEW_LINE);
        // adding import of activejbdc entity
        stringBuilder.append("import ").append(packageName).append('.').append(className).append(';').append(NEW_LINE);
        // creating class name
        stringBuilder.append("public class ").append(className).append("Wrapper").append(" {").append(NEW_LINE);
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
        generateGetObject(stringBuilder, className, activeJdbcObjectName);
        // add to method
        // add toString (using getters)
        // add equals and hashcode (using getters)
        // close the class
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    private static void generateGetObject(StringBuilder stringBuilder, String className, String activeJdbcObjectName) {
        stringBuilder.append(buildMethodGetObject(className, activeJdbcObjectName));
    }

    private static void generateGetters(StringBuilder stringBuilder, List<AnnotationMirror> annotationMirrors, String activeJdbcObjectName) {
        annotationMirrors.forEach(annotationMirror -> {
            AnnotationValue clazz = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz");
            AnnotationValue field = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "field");
            String columnName = field.getValue().toString();
            String getterName = buildMethodName(columnName, "get");
            stringBuilder.append(buildGetter(getterName, clazz.getValue().toString(), activeJdbcObjectName, columnName));
        });
    }

    private static void generateSetters(StringBuilder stringBuilder, List<AnnotationMirror> annotationMirrors, String activeJdbcObjectName) {
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
