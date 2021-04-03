package activejdbc.pojo.builder.annotation.processor;

import activejdbc.pojo.builder.annotation.processor.util.AnnotationValueExtractor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        // add to method
        // add toString (using getters)
        // add equals and hashcode (using getters)
        // close the class
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    private static void generateGetters(StringBuilder stringBuilder, List<AnnotationMirror> annotationMirrors, String activeJdbcObjectName) {
        annotationMirrors.forEach(annotationMirror -> {
            AnnotationValue clazz = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz");
            AnnotationValue field = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "field");
            stringBuilder.append("public ").append(clazz.getValue()).append(" ").append(buildMethodName(field.getValue().toString(), "get"))
                    .append("() {").append(NEW_LINE)
                    .append("return (").append(clazz.getValue()).append(") ")
                    .append(activeJdbcObjectName).append(".get(\"").append(field.getValue().toString()).append("\");").append(NEW_LINE)
                    .append('}');
        });
    }

    private static void generateSetters(StringBuilder stringBuilder, List<AnnotationMirror> annotationMirrors, String activeJdbcObjectName) {
        annotationMirrors.forEach(
                annotationMirror -> {
                    AnnotationValue clazz = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz");
                    AnnotationValue field = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "field");
                    stringBuilder.append("public void ").append(buildMethodName(field.getValue().toString(), "set"))
                            .append('(').append(clazz.getValue()).append(' ').append(buildPropertyName(field.getValue().toString())).append(" ) {").append(NEW_LINE)
                            .append(activeJdbcObjectName).append(".set(\"").append(field.getValue().toString()).append("\", ").append(buildPropertyName(field.getValue().toString())).append(");").append(NEW_LINE)
                            .append('}').append(NEW_LINE);

                });
    }

    private static void addImportsFromAnnotation(StringBuilder stringBuilder, List<AnnotationMirror> annotationMirrors) {
        Set<AnnotationValue> annotationValuesWithClasses = annotationMirrors.stream()
                .map(annotationMirror -> AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz"))
                .collect(Collectors.toSet());
        for (AnnotationValue annotationValuesWithClass : annotationValuesWithClasses) {
            stringBuilder.append("import ")
                    .append(annotationValuesWithClass.getValue())
                    .append(';').append(NEW_LINE);
        }
    }
}
