package activejdbc.pojo.builder.annotation.processor.util;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

public final class StringTemplates {
    /**
     * 0. return type
     * 1. method name
     * 2. class for casting
     * 3. activejdbc object
     * 4. column name
     */
    private static final String GETTER_TEMPLATE = "public %s %s() {%n" +
            "return (%s) %s.get(\"%s\");%n" +
            "}%n";
    /**
     * 0. setters name
     * 1. setters type
     * 2. property name
     * 3. activejdbc object
     * 4. column name
     * 5. property name
     */
    private static final String SETTER_TEMPLATE = "public void %s(%s %s) {%n" +
            "%s.set(\"%s\", %s);%n" +
            "}%n";
    /**
     * 0. Class name
     * 1. activejdbc object
     */
    private static final String METHOD_GET_OBJECT_TEMPLATE = "public %s getActivejdbcObject() {%n" +
            "return %s;%n" +
            "}";
    /**
     * 0. Class name
     * 1. activejdbc object
     */
    private static final String METHOD_SET_OBJECT_TEMPLATE = "public void setActivejdbcObject(%s %s) {%n" +
            "this.%s = %s;%n" +
            "}";
    /**
     * 0. toString implementation
     */
    private static final String TO_STRING_METHOD_TEMPLATE = "public java.lang.String toString() {%n" +
            "return %s;%n" +
            "}";

    /**
     * 0. generated checks for equals
     */
    private static final String EQUALS_METHOD_TEMPLATE = "public boolean equals(Object o) {%n" +
            "if (this == o) return true;%n" +
            "if (o == null || getClass() != o.getClass()) return false;%n" +
            "TestClassWrapper that = (TestClassWrapper) o;%n" +
            "return %s;%n" +
            "}";
    /**
     * 0. generated call of getters
     */
    private static final String HASH_CODE_METHOD_TRMPLATE = "public int hashCode() {%n" +
            "return java.util.Objects.hash(%s);%n" +
            "}";

    public static String buildHashCode(Collection<String> getters) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (String getter : getters) {
            stringJoiner.add(String.format("this.%s()", getter));
        }
        return String.format(HASH_CODE_METHOD_TRMPLATE, stringJoiner.toString());
    }

    public static String buildEquals(Collection<String> getters) {
        StringJoiner stringJoiner = new StringJoiner(" && ");
        for (String getter : getters) {
            stringJoiner.add(String.format("%njava.util.Objects.equals(this.%s(), that.%s())", getter, getter));
        }
        return String.format(EQUALS_METHOD_TEMPLATE, stringJoiner.toString());
    }

    public static String buildToString(Map<String, String> propertyNamesAndGetters) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('"')
                .append('{');
        StringJoiner stringJoiner = new StringJoiner(" + \", ");
        propertyNamesAndGetters.forEach((propertyName, getter) ->
                stringJoiner.add(String.format("'%s' = \" + this.%s()%n", propertyName, getter)));
        stringBuilder.append(stringJoiner.toString());
        stringBuilder.append(" + \"}\"");
        return String.format(TO_STRING_METHOD_TEMPLATE, stringBuilder.toString());
    }

    public static String buildMethodSetObject(String activejdbcClassName, String activeJdbcObjectName) {
        return String.format(METHOD_SET_OBJECT_TEMPLATE, activejdbcClassName, activeJdbcObjectName, activeJdbcObjectName, activeJdbcObjectName);
    }

    public static String buildGetter(String methodName, String type, String activejdbcObjectName, String columnName) {
        return String.format(GETTER_TEMPLATE, type, methodName, type, activejdbcObjectName, columnName);
    }

    public static String buildSetter(String methodName, String type, String propertyName, String activejdbcObjectName, String columnName) {
        return String.format(SETTER_TEMPLATE, methodName, type, propertyName, activejdbcObjectName, columnName, propertyName);
    }

    public static String buildMethodGetObject(String activejdbcClassName, String activeJdbcObjectName) {
        return String.format(METHOD_GET_OBJECT_TEMPLATE, activejdbcClassName, activeJdbcObjectName);
    }
}
