package activejdbc.pojo.builder.annotation.processor.builder.strategy.getter;

import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

public class BooleanGetterBuilderStrategy implements GetterBuilderStrategy{
    /**
     * 1. return type
     * 2. method name
     * 3. activejdbc object
     * 4. column name
     */
    private static final String BOOLEAN_GETTER_TEMPLATE = "public %s %s() {%n" +
            "return %s.getBoolean(\"%s\");%n" +
            "}%n";

    @Override
    public String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, "get");
        return String.format(BOOLEAN_GETTER_TEMPLATE, type, methodName, activejdbcObjectName, columnName);
    }
}
