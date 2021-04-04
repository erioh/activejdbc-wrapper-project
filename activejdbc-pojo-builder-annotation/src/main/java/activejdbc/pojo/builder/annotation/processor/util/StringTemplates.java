package activejdbc.pojo.builder.annotation.processor.util;

public final class StringTemplates {
    /**
     * 0. return type
     * 1. method name
     * 2. class for casting
     * 3. activejdbc object
     * 4. column name
     */
    public static final String GETTER_TEMPLATE = "public %s %s() {%n" +
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
    public static final String SETTER_TEMPLATE = "public void %s(%s %s) {%n" +
            "%s.set(\"%s\", %s);%n" +
            "}%n";
    /**
     * 0. Class name
     * 1. activejdbc object
     */
    public static final String METHOD_GET_OBJECT_TEMPLATE = "public %s getActivejdbcObject() {%n" +
            "return %s;%n" +
            "}";
    /**
     * 0. Class name
     * 1. activejdbc object
     */
    public static final String METHOD_SET_OBJECT_TEMPLATE = "public void setActivejdbcObject(%s %s) {%n" +
            "this.%s = %s;%n" +
            "}";
    /**
     * 0. toString implementation
     */
    public static final String TO_STRING_METHOD_TEMPLATE = "public java.lang.String toString() {%n" +
            "return %s;%n" +
            "}";

    /**
     * 0. wrapper class name
     * 1. wrapper class name
     * 2. generated checks for equals
     */
    public static final String EQUALS_METHOD_TEMPLATE = "public boolean equals(Object o) {%n" +
            "if (this == o) return true;%n" +
            "if (o == null || getClass() != o.getClass()) return false;%n" +
            "%s that = (%s) o;%n" +
            "return %s;%n" +
            "}";
    /**
     * 0. generated call of getters
     */
    public static final String HASH_CODE_METHOD_TRMPLATE = "public int hashCode() {%n" +
            "return java.util.Objects.hash(%s);%n" +
            "}";
    /**
     * 0. package name
     * 1. import activejbdc class package
     * 2. activejbdc object class for import
     * 3. wrapper class name
     * 4. activejbdc object class
     * 5. acitvejdbc object name
     * 6. activejbdc object class
     * 7. methods
     */
    public static final String CLASS_TEMPLATE = "package %s;%n" +
            "import %s.%s;%n" +
            "public class %s {%n" +
            "%s %s = new %s();%n" +
            "%s%n" +
            "}";


}
