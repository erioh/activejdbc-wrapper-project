package activejdbc.pojo.builder.annotation.processor.builder.strategy.getter;

import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

public class LocalDateGetterBuilderStrategy implements GetterBuilderStrategy {

    /**
     * 0. return type
     * 1. method name
     * 2. activejdbc object
     * 3. column name
     */
    private static final String LOCAL_DATE_GETTER_TEMPLATE = "public %s %s() {%n" +
            "return java.util.Optional.ofNullable(%s.getDate(\"%s\"))%n" +
            ".map(java.sql.Date::toLocalDate)%n" +
            ".orElse(null);%n" +
            "}%n";

    @Override
    public String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, "get");
        return String.format(LOCAL_DATE_GETTER_TEMPLATE, type, methodName, activejdbcObjectName, columnName);
    }
}
