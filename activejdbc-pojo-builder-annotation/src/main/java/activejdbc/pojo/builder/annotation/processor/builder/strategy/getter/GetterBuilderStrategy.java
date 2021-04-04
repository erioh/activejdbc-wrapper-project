package activejdbc.pojo.builder.annotation.processor.builder.strategy.getter;

public interface GetterBuilderStrategy {
    String buildGetterBody(String type, String methodName, String activejdbcObjectName, String columnName);
}
