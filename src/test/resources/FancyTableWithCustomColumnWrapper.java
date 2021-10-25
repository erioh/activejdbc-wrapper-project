package com.sdemenkov;
import com.sdemenkov.FancyTableWithCustomColumn;
public class FancyTableWithCustomColumnWrapper extends activejdbc.wrapper.annotation.processor.builder.ActivejdbcWrapper<FancyTableWithCustomColumn>{
    private FancyTableWithCustomColumn fancyTableWithCustomColumn;
    public FancyTableWithCustomColumnWrapper() {
        this.fancyTableWithCustomColumn = new FancyTableWithCustomColumn();
    }
    public FancyTableWithCustomColumnWrapper(FancyTableWithCustomColumn fancyTableWithCustomColumn) {
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
        return "{customColumn = " + "'" + this.getCustomColumn() + "'"
                + "}";
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FancyTableWithCustomColumnWrapper that = (FancyTableWithCustomColumnWrapper) o;
        return
                java.util.Objects.equals(this.getCustomColumn(), that.getCustomColumn());
    }
    public int hashCode() {
        return java.util.Objects.hash(this.getCustomColumn());
    }

    public static FancyTableWithCustomColumnWrapperBuilder builder() {
        return new FancyTableWithCustomColumnWrapperBuilder();
    }
    public static class FancyTableWithCustomColumnWrapperBuilder {
        private final FancyTableWithCustomColumnWrapper fancyTableWithCustomColumnWrapper = new FancyTableWithCustomColumnWrapper();
        public FancyTableWithCustomColumnWrapperBuilder withCustomColumn(java.lang.Long customColumn) {
            this.fancyTableWithCustomColumnWrapper.setCustomColumn(customColumn);
            return this;
        }

        public FancyTableWithCustomColumnWrapper build() {
            return fancyTableWithCustomColumnWrapper;
        }
    }
}