package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;

public class TimestampSetterBuilderStrategy implements SetterBuilderStrategy {
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

    @Override
    public String buildSetterBody(String type, String columnName, String activejdbcObjectName) {
        String propertyName = StringUtils.buildPropertyNameFromColumnName(columnName);
        String methodName = StringUtils.buildMethodName(columnName, PREFIX);
        return String.format(SETTER_TEMPLATE, methodName, type, propertyName, activejdbcObjectName, columnName, propertyName);
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.singleton(Timestamp.class);
    }
}
