package activejdbc.pojo.builder.annotation.processor.builder.strategy.getter;

public class DefaultGetterBuilderStrategies implements GetterBuilderStrategy {
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
    @Override
    public String buildGetterBody(String type, String methodName, String activejdbcObjectName, String columnName) {
        return String.format(GETTER_TEMPLATE, type, methodName, type, activejdbcObjectName, columnName);
    }
}
