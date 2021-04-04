package activejdbc.pojo.builder.annotation.processor.builder.strategy.setter;

public interface SetterBuilderStrategy {
    String buildSetterBody(String type, String columnName, String activejdbcObjectName);
}
