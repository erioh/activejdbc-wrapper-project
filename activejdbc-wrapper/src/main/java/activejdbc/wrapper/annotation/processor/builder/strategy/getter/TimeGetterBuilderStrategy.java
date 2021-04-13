package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.sql.Time;
import java.util.Collections;
import java.util.Set;

public class TimeGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. activejdbc object
     * 4. column name
     */
    private static final String GETTER_TEMPLATE = "public %s %s() {%n" +
            "return new java.sql.Time(%s.getDate(\"%s\").getTime());%n" +
            "}%n";

    @Override
    public String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, PREFIX);
        return String.format(GETTER_TEMPLATE, type, methodName, activejdbcObjectName, columnName);
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.singleton(Time.class);
    }
}
