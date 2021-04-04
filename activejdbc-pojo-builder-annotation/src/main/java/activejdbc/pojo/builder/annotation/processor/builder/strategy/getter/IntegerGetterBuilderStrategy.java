package activejdbc.pojo.builder.annotation.processor.builder.strategy.getter;

import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

public class IntegerGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. activejdbc object
     * 4. column name
     */
    private static final String GETTER_TEMPLATE = "public %s %s() {%n" +
            "return %s.getInteger(\"%s\");%n" +
            "}%n";


    @Override
    public String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, "get");
        return String.format(GETTER_TEMPLATE, type, methodName, activejdbcObjectName, columnName);
    }
}
