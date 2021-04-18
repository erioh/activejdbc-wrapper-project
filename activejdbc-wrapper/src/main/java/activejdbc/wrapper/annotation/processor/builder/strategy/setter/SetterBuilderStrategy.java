package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.Set;

public interface SetterBuilderStrategy {

    default String buildSetterBody(String type, String columnName, String activejdbcObjectName) {
        String propertyName = StringUtils.buildPropertyNameFromColumnName(columnName);
        String methodName = StringUtils.buildMethodName(columnName, "set");
        return String.format(getTemplate(), methodName, type, propertyName, activejdbcObjectName, columnName, propertyName);
    }

    String getTemplate();

    Set<Class<?>> typesToApply();
}
