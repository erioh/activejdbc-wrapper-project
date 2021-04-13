package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import java.util.Set;

public interface SetterBuilderStrategy {
    String PREFIX = "set";
    String buildSetterBody(String type, String columnName, String activejdbcObjectName);
    Set<Class<?>> typesToApply();
}
