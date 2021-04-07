package activejdbc.pojo.builder.annotation.processor.builder.strategy.setter;

import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DateSetterBuilderStrategy implements SetterBuilderStrategy {
    /**
     * 1. setters name
     * 2. setters type
     * 3. property name
     * 4. activejdbc object
     * 5. column name
     * 6. property name
     */
    public static final String SETTER_TEMPLATE = "public void %s(%s %s) {%n" +
            "%s.setDate(\"%s\", %s);%n" +
            "}%n";

    @Override
    public String buildSetterBody(String type, String columnName, String activejdbcObjectName) {
        String propertyName = StringUtils.buildPropertyNameFromColumnName(columnName);
        String methodName = StringUtils.buildMethodName(columnName, "set");
        return String.format(SETTER_TEMPLATE, methodName, type, propertyName, activejdbcObjectName, columnName, propertyName);
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return new HashSet<>(Arrays.asList(LocalDate.class, Date.class));
    }
}
