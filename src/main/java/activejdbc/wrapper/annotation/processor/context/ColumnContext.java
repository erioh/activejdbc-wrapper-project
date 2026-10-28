package activejdbc.wrapper.annotation.processor.context;

import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.Objects;

public final class ColumnContext {
    private final String clazz;
    private final String columnName;
    private final String desiredFieldName;
    private final boolean enumType;

    public ColumnContext(String clazz, String columnName, String desiredFieldName) {
        this(clazz, columnName, desiredFieldName, false);
    }

    public ColumnContext(String clazz, String columnName, String desiredFieldName, boolean enumType) {
        this.clazz = clazz;
        this.columnName = columnName;
        this.desiredFieldName = desiredFieldName;
        this.enumType = enumType;
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

    /**
     * Whether clazz is an enum. Enums are user types, so no strategy can be registered for them by class name.
     */
    public boolean isEnumType() {
        return enumType;
    }

    public String getPropertyName() {
        return StringUtils.isBlank(desiredFieldName)
                ? StringUtils.buildPropertyNameFromColumnName(columnName)
                : desiredFieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnContext that = (ColumnContext) o;
        return Objects.equals(getClazz(), that.getClazz()) && Objects.equals(getColumnName(), that.getColumnName()) && Objects.equals(getDesiredFieldName(), that.getDesiredFieldName()) && enumType == that.enumType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClazz(), getColumnName(), getDesiredFieldName(), enumType);
    }
}
