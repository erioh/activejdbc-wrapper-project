package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import java.util.Set;

public interface GetterBuilderStrategy {
    String buildGetterBody(String type, String columnName, String activejdbcObjectName);

    Set<Class<?>> typesToApply();
}
