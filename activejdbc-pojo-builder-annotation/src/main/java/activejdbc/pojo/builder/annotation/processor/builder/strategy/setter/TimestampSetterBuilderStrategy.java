package activejdbc.pojo.builder.annotation.processor.builder.strategy.setter;

import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

public class TimestampSetterBuilderStrategy implements SetterBuilderStrategy{
    /**
     * 1. setters name
     * 2. setters type
     * 3. property name
     * 4. activejdbc object
     * 5. column name
     * 6. property name
     */
    public static final String SETTER_TEMPLATE = "public void %s(%s %s) {%n" +
            "%s.setTimestamp(\"%s\", %s);%n" +
            "}%n";
    /**
     * 1. Class name
     * 2. activejdbc object
     */
    @Override
    public String buildSetterBody(String type, String columnName, String activejdbcObjectName) {
        String propertyName = StringUtils.buildPropertyNameFromColumnName(columnName);
        String methodName = StringUtils.buildMethodName(columnName, "set");
        return String.format(SETTER_TEMPLATE, methodName, type, propertyName, activejdbcObjectName, columnName, propertyName);
    }
}
