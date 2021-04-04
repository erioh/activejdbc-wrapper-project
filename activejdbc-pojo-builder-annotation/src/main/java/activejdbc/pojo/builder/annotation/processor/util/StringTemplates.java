package activejdbc.pojo.builder.annotation.processor.util;

public final class StringTemplates {

    public static final String METHOD_GET_OBJECT_TEMPLATE = "protected %s getActivejdbcObject() {%n" +
            "return %s;%n" +
            "}%n";
    /**
     * 1. Class name
     * 2. activejdbc object
     */
    public static final String METHOD_SET_OBJECT_TEMPLATE = "protected void setActivejdbcObject(%s %s) {%n" +
            "this.%s = %s;%n" +
            "}%n";
    /**
     * 1. toString implementation
     */
    public static final String TO_STRING_METHOD_TEMPLATE = "public java.lang.String toString() {%n" +
            "return %s;%n" +
            "}%n";

    /**
     * 1. wrapper class name
     * 2. wrapper class name
     * 3. generated checks for equals
     */
    public static final String EQUALS_METHOD_TEMPLATE = "public boolean equals(Object o) {%n" +
            "if (this == o) return true;%n" +
            "if (o == null || getClass() != o.getClass()) return false;%n" +
            "%s that = (%s) o;%n" +
            "return %s;%n" +
            "}%n";
    /**
     * 1. generated call of getters
     */
    public static final String HASH_CODE_METHOD_TRMPLATE = "public int hashCode() {%n" +
            "return java.util.Objects.hash(%s);%n" +
            "}%n";
    /**
     * 1. package name
     * 2. import activejbdc class package
     * 3. activejbdc object class for import
     * 4. wrapper class name
     * 5. activejdbc object class
     * 6  activejdbc object class
     * 7. activejdbc object name
     * 8. constructors
     * 9. methods
     */
    public static final String CLASS_TEMPLATE = "package %s;%n" +
            "import %s.%s;%n" +
            "public class %s extends activejdbc.pojo.builder.annotation.processor.builder.ActivejdbcWrapper<%s>{%n" +
            "%s %s;%n" +
            "%s%n" +
            "%s%n" +
            "}%n";

    /**
     * 1. Wrapper name
     * 2. activejdbc object name
     * 3. activejdbc object class
     */
    public static final String CONSTRUCTOR_WITHOUT_PARAMETERS_TEMPLATE = " public %s() {%n" +
            "this.%s = new %s();%n" +
            "}%n";

    /**
     * 1. Wrapper name
     * 2. activejdbc object class
     * 2. activejdbc object name
     * 3. activejdbc object name
     * 4. activejdbc object name
     */
    public static final String CONSTRUCTOR_WITH_PARAMETER_TEMPLATE = " public %s(%s %s) {%n" +
            "this.%s = %s;%n" +
            "}%n";
}
