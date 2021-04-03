package activejdbc.pojo.builder.annotation.processor;

import javax.lang.model.element.AnnotationMirror;
import java.util.List;

public class ActiveJdbcRequiredPropertyWrapperFactory {
    private static final String NEW_LINE = System.getProperty("line.separator");

    public static String build(String packageName, String className, List<AnnotationMirror> annotationMirrors) {
        // adding package
        StringBuilder stringBuilder = new StringBuilder()
                .append("package ").append(packageName).append(';').append(NEW_LINE);
        // adding import of activejbdc entity
        stringBuilder.append("import ").append(packageName).append('.').append(className).append(';').append(NEW_LINE);
        // adding imports of types
        addImportsFromAnnotation(stringBuilder, annotationMirrors);
        // creating class name
        stringBuilder.append("public class ").append(className).append("Wrapper").append(" {").append(NEW_LINE);
        // create private instance of activejdbc class
        // add setters
        // add getters
        // add from method
        // add to method
        // add toString (using getters)
        // add equals and hashcode (using getters)
        // close the class
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    private static void addImportsFromAnnotation(StringBuilder stringBuilder, List<AnnotationMirror> annotations) {
//        for (ActiveJdbcRequiredProperty annotation : Arrays.stream(annotations).collect(Collectors.toSet())) {
//            Class<?> clazz = annotation.clazz();
//            stringBuilder.append("import ")
//                    .append(clazz.getName())
//                    .append(';').append(NEW_LINE);
//        }
    }
}
