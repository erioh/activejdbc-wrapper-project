package com.sdemenkov;
import com.sdemenkov.FancyTable;
@javax.annotation.processing.Generated("activejdbc.wrapper.annotation.processor.ActiveJdbcRequiredPropertyProcessor")
public class FancyTableNewWrapperSuffix extends activejdbc.wrapper.ActivejdbcWrapper<FancyTable> {
    public static final java.lang.String FANCY_TABLE_ID = "FANCY_TABLE_ID";
    private FancyTable fancyTable;
    public FancyTableNewWrapperSuffix() {
        this.fancyTable = new FancyTable();
    }
    public FancyTableNewWrapperSuffix(FancyTable fancyTable) {
        this.fancyTable = fancyTable;
    }

    public java.lang.Long getFancyTableId() {
        return fancyTable.getLong("FANCY_TABLE_ID");
    }
    public void setFancyTableId(java.lang.Long fancyTableId) {
        this.fancyTable.set("FANCY_TABLE_ID", fancyTableId);
    }
    protected FancyTable getActivejdbcObject() {
        return fancyTable;
    }
    public static java.util.List<FancyTableNewWrapperSuffix> wrap(java.util.Collection<? extends FancyTable> models) {
        java.util.List<FancyTableNewWrapperSuffix> wrappers = new java.util.ArrayList<>(models.size());
        for (FancyTable model : models) {
            wrappers.add(new FancyTableNewWrapperSuffix(model));
        }
        return wrappers;
    }
    public java.lang.String toString() {
        return "{fancyTableId = " + (this.getFancyTableId() == null ? null : "'" + this.getFancyTableId() + "'")
                + "}";
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FancyTableNewWrapperSuffix that = (FancyTableNewWrapperSuffix) o;
        return
                java.util.Objects.deepEquals(this.getFancyTableId(), that.getFancyTableId());
    }
    public int hashCode() {
        return java.util.Arrays.deepHashCode(new java.lang.Object[]{this.getFancyTableId()});
    }

    public static FancyTableNewWrapperSuffixBuilder builder() {
        return new FancyTableNewWrapperSuffixBuilder();
    }
    public static class FancyTableNewWrapperSuffixBuilder {
        private final FancyTableNewWrapperSuffix fancyTableNewWrapperSuffix = new FancyTableNewWrapperSuffix();
        public FancyTableNewWrapperSuffixBuilder withFancyTableId(java.lang.Long fancyTableId) {
            this.fancyTableNewWrapperSuffix.setFancyTableId(fancyTableId);
            return this;
        }

        public FancyTableNewWrapperSuffix build() {
            return fancyTableNewWrapperSuffix;
        }
    }
}