package com.sdemenkov;
import com.sdemenkov.FancyTable;
public class FancyTableWrapper extends activejdbc.wrapper.annotation.processor.builder.ActivejdbcWrapper<FancyTable>{
    private FancyTable fancyTable;
    public FancyTableWrapper() {
        this.fancyTable = new FancyTable();
    }
    public FancyTableWrapper(FancyTable fancyTable) {
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
        FancyTableWrapper that = (FancyTableWrapper) o;
        return
                java.util.Objects.equals(this.getFancyTableId(), that.getFancyTableId());
    }
    public int hashCode() {
        return java.util.Objects.hash(this.getFancyTableId());
    }

    public static FancyTableWrapperBuilder builder() {
        return new FancyTableWrapperBuilder();
    }
    public static class FancyTableWrapperBuilder {
        private final FancyTableWrapper fancyTableWrapper = new FancyTableWrapper();
        public FancyTableWrapperBuilder withFancyTableId(java.lang.Long fancyTableId) {
            this.fancyTableWrapper.setFancyTableId(fancyTableId);
            return this;
        }

        public FancyTableWrapper build() {
            return fancyTableWrapper;
        }
    }
}