package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.Set;

public interface GetterBuilderStrategy {

    default String buildGetterBody(String type, String columnName, String activejdbcObjectName) {
        String methodName = StringUtils.buildMethodName(columnName, "get");
        return String.format(getTemplate(), type, methodName, activejdbcObjectName, columnName);
    }

    String getTemplate();

    Set<Class<?>> typesToApply();
}
