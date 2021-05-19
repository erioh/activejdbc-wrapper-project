package activejdbc.wrapper.annotation.processor.util;

public final class StringTemplates {

    public static final String METHOD_GET_OBJECT_TEMPLATE = "protected %s getActivejdbcObject() {%n" +
            "return %s;%n" +
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
            "return%s;%n" +
            "}%n";
    /**
     * 1. generated call of getters
     */
    public static final String HASH_CODE_METHOD_TEMPLATE = "public int hashCode() {%n" +
            "return java.util.Objects.hash(%s);%n" +
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
