package activejdbc.pojo.builder.annotation.processor.builder.strategy.getter;

import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

public class LocalTimeGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. activejdbc object
     * 4. column name
     */
    private static final String LOCAL_TIME_GETTER_TEMPLATE = "public %s %s() {%n" +
            "return java.util.Optional.ofNullable(%s.getTime(\"%s\"))%n" +
            ".map(java.sql.Time::toLocalTime)%n" +
            ".orElse(null);%n" +
            "}%n";

    @Override
    public String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, "get");
        return String.format(LOCAL_TIME_GETTER_TEMPLATE, type, methodName, activejdbcObjectName, columnName);
    }
}
