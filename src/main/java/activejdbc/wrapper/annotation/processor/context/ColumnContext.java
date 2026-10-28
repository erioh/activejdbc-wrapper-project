package activejdbc.wrapper.annotation.processor.context;

import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.Objects;

public final class ColumnContext {
    private final String clazz;
    private final String columnName;
    private final String desiredFieldName;
    private final boolean enumType;
    private final boolean readOnly;
    private final boolean excludedFromEqualsAndHashCode;
    // null without a converter
    private final Converter converter;

    public ColumnContext(String clazz, String columnName, String desiredFieldName) {
        this(clazz, columnName, desiredFieldName, false);
    }

    public ColumnContext(String clazz, String columnName, String desiredFieldName, boolean enumType) {
        this(clazz, columnName, desiredFieldName, enumType, false, false);
    }

    public ColumnContext(String clazz, String columnName, String desiredFieldName, boolean enumType,
                         boolean readOnly, boolean excludedFromEqualsAndHashCode) {
        this.clazz = clazz;
        this.columnName = columnName;
        this.desiredFieldName = desiredFieldName;
        this.enumType = enumType;
        this.readOnly = readOnly;
        this.excludedFromEqualsAndHashCode = excludedFromEqualsAndHashCode;
        this.converter = null;
    }

    private ColumnContext(ColumnContext column, String clazz, Converter converter) {
        this.clazz = clazz;
        this.columnName = column.columnName;
        this.desiredFieldName = column.desiredFieldName;
        this.enumType = column.enumType;
        this.readOnly = column.readOnly;
        this.excludedFromEqualsAndHashCode = column.excludedFromEqualsAndHashCode;
        this.converter = converter;
    }

    /**
     * @param propertyType the converter's property type, which may be generic ({@code java.util.List<java.lang.String>})
     *                     where clazz can only be raw
     * @param converter    the converter
     * @return a copy of this column with the converter
     */
    public ColumnContext withConverter(String propertyType, Converter converter) {
        return new ColumnContext(this, propertyType, converter);
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

    /**
     * Whether only a getter is generated: no setter, no builder method.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isExcludedFromEqualsAndHashCode() {
        return excludedFromEqualsAndHashCode;
    }

    public Converter getConverter() {
        return converter;
    }

    public boolean hasConverter() {
        return converter != null;
    }

    /**
     * Name of the static field holding the converter instance in the wrapper.
     */
    public String getConverterFieldName() {
        return getPropertyName() + "Converter";
    }

    /**
     * The column as the converter's database side sees it: its type is the converter's {@code D}. Used to pick the
     * getter and setter strategies that read and write the column.
     */
    public ColumnContext getDatabaseColumn() {
        return new ColumnContext(converter.getDatabaseClazz(), columnName, desiredFieldName, converter.isDatabaseEnumType());
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
        return Objects.equals(getClazz(), that.getClazz()) && Objects.equals(getColumnName(), that.getColumnName()) && Objects.equals(getDesiredFieldName(), that.getDesiredFieldName()) && enumType == that.enumType
                && readOnly == that.readOnly && excludedFromEqualsAndHashCode == that.excludedFromEqualsAndHashCode
                && Objects.equals(converter, that.converter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClazz(), getColumnName(), getDesiredFieldName(), enumType, readOnly, excludedFromEqualsAndHashCode, converter);
    }

    /**
     * A {@code ColumnConverter<T, D>} named in the annotation.
     */
    public static final class Converter {
        private final String className;
        private final String databaseClazz;
        private final boolean databaseEnumType;

        /**
         * @param className        canonical name of the converter class
         * @param databaseClazz    canonical name of D
         * @param databaseEnumType whether D is an enum
         */
        public Converter(String className, String databaseClazz, boolean databaseEnumType) {
            this.className = className;
            this.databaseClazz = databaseClazz;
            this.databaseEnumType = databaseEnumType;
        }

        public String getClassName() {
            return className;
        }

        public String getDatabaseClazz() {
            return databaseClazz;
        }

        public boolean isDatabaseEnumType() {
            return databaseEnumType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Converter that = (Converter) o;
            return className.equals(that.className) && databaseClazz.equals(that.databaseClazz) && databaseEnumType == that.databaseEnumType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(className, databaseClazz, databaseEnumType);
        }
    }
}
