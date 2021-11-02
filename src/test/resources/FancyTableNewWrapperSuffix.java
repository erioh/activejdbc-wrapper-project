package com.sdemenkov;
import com.sdemenkov.FancyTable;
public class FancyTableNewWrapperSuffix extends activejdbc.wrapper.annotation.processor.builder.ActivejdbcWrapper<FancyTable>{
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
        fancyTable.set("FANCY_TABLE_ID", fancyTableId);
    }
    protected FancyTable getActivejdbcObject() {
        return fancyTable;
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
                java.util.Objects.equals(this.getFancyTableId(), that.getFancyTableId());
    }
    public int hashCode() {
        return java.util.Objects.hash(this.getFancyTableId());
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