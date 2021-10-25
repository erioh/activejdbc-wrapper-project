package activejdbc.wrapper.annotation.processor;

import java.util.Objects;

public final class ColumnContext {
    private final String clazz;
    private final String columnName;
    private final String desiredFieldName;

    public ColumnContext(String clazz, String columnName, String desiredFieldName) {
        this.clazz = clazz;
        this.columnName = columnName;
        this.desiredFieldName = desiredFieldName;
    }

    public String getClazz() {
        return clazz;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDesiredFieldName() {
        return desiredFieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnContext that = (ColumnContext) o;
        return Objects.equals(getClazz(), that.getClazz()) && Objects.equals(getColumnName(), that.getColumnName()) && Objects.equals(getDesiredFieldName(), that.getDesiredFieldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClazz(), getColumnName(), getDesiredFieldName());
    }
}
