package com.sdemenkov;
import com.sdemenkov.FancyTableWithCustomColumn;
@javax.annotation.processing.Generated("activejdbc.wrapper.annotation.processor.ActiveJdbcRequiredPropertyProcessor")
public class FancyTableWithCustomColumnNewWrapperSuffix extends activejdbc.wrapper.ActivejdbcWrapper<FancyTableWithCustomColumn> {
    public static final java.lang.String FANCY_TABLE_ID = "FANCY_TABLE_ID";
    private FancyTableWithCustomColumn fancyTableWithCustomColumn;
    public FancyTableWithCustomColumnNewWrapperSuffix() {
        this.fancyTableWithCustomColumn = new FancyTableWithCustomColumn();
    }
    public FancyTableWithCustomColumnNewWrapperSuffix(FancyTableWithCustomColumn fancyTableWithCustomColumn) {
        this.fancyTableWithCustomColumn = fancyTableWithCustomColumn;
    }

    public java.lang.Long getCustomColumn() {
        return fancyTableWithCustomColumn.getLong("FANCY_TABLE_ID");
    }
    public void setCustomColumn(java.lang.Long customColumn) {
        this.fancyTableWithCustomColumn.set("FANCY_TABLE_ID", customColumn);
    }
    protected FancyTableWithCustomColumn getActivejdbcObject() {
        return fancyTableWithCustomColumn;
    }
    public static java.util.List<FancyTableWithCustomColumnNewWrapperSuffix> wrap(java.util.Collection<? extends FancyTableWithCustomColumn> models) {
        java.util.List<FancyTableWithCustomColumnNewWrapperSuffix> wrappers = new java.util.ArrayList<>(models.size());
        for (FancyTableWithCustomColumn model : models) {
            wrappers.add(new FancyTableWithCustomColumnNewWrapperSuffix(model));
        }
        return wrappers;
    }
    public java.lang.String toString() {
        return "{customColumn = " + (this.getCustomColumn() == null ? null : "'" + this.getCustomColumn() + "'")
                + "}";
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FancyTableWithCustomColumnNewWrapperSuffix that = (FancyTableWithCustomColumnNewWrapperSuffix) o;
        return
                java.util.Objects.deepEquals(this.getCustomColumn(), that.getCustomColumn());
    }
    public int hashCode() {
        return java.util.Arrays.deepHashCode(new java.lang.Object[]{this.getCustomColumn()});
    }

    public static FancyTableWithCustomColumnNewWrapperSuffixBuilder builder() {
        return new FancyTableWithCustomColumnNewWrapperSuffixBuilder();
    }
    public static class FancyTableWithCustomColumnNewWrapperSuffixBuilder {
        private final FancyTableWithCustomColumnNewWrapperSuffix fancyTableWithCustomColumnNewWrapperSuffix = new FancyTableWithCustomColumnNewWrapperSuffix();
        public FancyTableWithCustomColumnNewWrapperSuffixBuilder withCustomColumn(java.lang.Long customColumn) {
            this.fancyTableWithCustomColumnNewWrapperSuffix.setCustomColumn(customColumn);
            return this;
        }

        public FancyTableWithCustomColumnNewWrapperSuffix build() {
            return fancyTableWithCustomColumnNewWrapperSuffix;
        }
    }

    public static FancyTableWithCustomColumnNewWrapperSuffixQuery query() {
        return new FancyTableWithCustomColumnNewWrapperSuffixQuery();
    }

    public static class FancyTableWithCustomColumnNewWrapperSuffixQuery extends activejdbc.wrapper.query.AbstractQuery<FancyTableWithCustomColumnNewWrapperSuffixQuery, FancyTableWithCustomColumn, FancyTableWithCustomColumnNewWrapperSuffix> {
        public FancyTableWithCustomColumnNewWrapperSuffixQuery() {
            super(FancyTableWithCustomColumn.class, FancyTableWithCustomColumnNewWrapperSuffix::new);
        }

        protected FancyTableWithCustomColumnNewWrapperSuffixQuery newQuery() {
            return new FancyTableWithCustomColumnNewWrapperSuffixQuery();
        }

        public activejdbc.wrapper.query.Condition<FancyTableWithCustomColumnNewWrapperSuffixQuery, java.lang.Long> customColumn() {
            return newCondition("FANCY_TABLE_ID");
        }
    }
}
