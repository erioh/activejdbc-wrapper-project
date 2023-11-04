package com.sdemenkov;
import com.sdemenkov.FancyTableWithCustomColumn;
public class FancyTableWithCustomColumnNewWrapperSuffix extends activejdbc.wrapper.ActivejdbcWrapper<FancyTableWithCustomColumn> {
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
        fancyTableWithCustomColumn.set("FANCY_TABLE_ID", customColumn);
    }
    protected FancyTableWithCustomColumn getActivejdbcObject() {
        return fancyTableWithCustomColumn;
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
                java.util.Objects.equals(this.getCustomColumn(), that.getCustomColumn());
    }
    public int hashCode() {
        return java.util.Objects.hash(this.getCustomColumn());
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
}