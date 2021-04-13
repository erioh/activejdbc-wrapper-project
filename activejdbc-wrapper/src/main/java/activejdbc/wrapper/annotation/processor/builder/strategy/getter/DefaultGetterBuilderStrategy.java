package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.Collections;
import java.util.Set;

public class DefaultGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. class for casting
     * 4. activejdbc object
     * 5. column name
     */
    private static final String DEFAULT_GETTER_TEMPLATE = "public %s %s() {%n" +
            "return (%s) %s.get(\"%s\");%n" +
            "}%n";

    @Override
    public String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, PREFIX);
        return String.format(DEFAULT_GETTER_TEMPLATE, type, methodName, type, activejdbcObjectName, columnName);
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.emptySet();
    }
}