package activejdbc.pojo.builder.annotation.processor.builder.strategy.getter;

import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

public class DefaultGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 0. return type
     * 1. method name
     * 2. class for casting
     * 3. activejdbc object
     * 4. column name
     */
    private static final String DEFAULT_GETTER_TEMPLATE = "public %s %s() {%n" +
            "return (%s) %s.get(\"%s\");%n" +
            "}%n";
    @Override
    public String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, "get");
        return String.format(DEFAULT_GETTER_TEMPLATE, type, methodName, type, activejdbcObjectName, columnName)
                ;
    }
}
