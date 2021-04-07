package activejdbc.pojo.builder.annotation.processor.builder.strategy.getter;

import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

import java.util.Collections;
import java.util.Set;

public class FloatGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. activejdbc object
     * 4. column name
     */
    private static final String GETTER_TEMPLATE = "public %s %s() {%n" +
            "return %s.getFloat(\"%s\");%n" +
            "}%n";

    @Override
    public String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, "get");
        return String.format(GETTER_TEMPLATE, type, methodName, activejdbcObjectName, columnName);
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.singleton(Float.class);
    }
}
