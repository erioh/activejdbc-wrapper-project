package com.sdemenkov;
import com.sdemenkov.FancyTable;
@javax.annotation.processing.Generated("activejdbc.wrapper.annotation.processor.ActiveJdbcRequiredPropertyProcessor")
public class FancyTableWrapper extends activejdbc.wrapper.ActivejdbcWrapper<FancyTable> {
    public static final java.lang.String FANCY_TABLE_ID = "FANCY_TABLE_ID";
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
        this.fancyTable.set("FANCY_TABLE_ID", fancyTableId);
    }
    protected FancyTable getActivejdbcObject() {
        return fancyTable;
    }
    public static java.util.List<FancyTableWrapper> wrap(java.util.Collection<? extends FancyTable> models) {
        java.util.List<FancyTableWrapper> wrappers = new java.util.ArrayList<>(models.size());
        for (FancyTable model : models) {
            wrappers.add(new FancyTableWrapper(model));
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
        FancyTableWrapper that = (FancyTableWrapper) o;
        return
                java.util.Objects.deepEquals(this.getFancyTableId(), that.getFancyTableId());
    }
    public int hashCode() {
        return java.util.Arrays.deepHashCode(new java.lang.Object[]{this.getFancyTableId()});
    }

    public static FancyTableWrapperBuilder builder() {
        return new FancyTableWrapperBuilder();
    }
    public FancyTableWrapperBuilder toBuilder() {
        return new FancyTableWrapperBuilder()
            .fancyTableId(this.getFancyTableId());
    }
    public static class FancyTableWrapperBuilder {
        private final FancyTableWrapper fancyTableWrapper = new FancyTableWrapper();
        public FancyTableWrapperBuilder fancyTableId(java.lang.Long fancyTableId) {
            this.fancyTableWrapper.setFancyTableId(fancyTableId);
            return this;
        }

        public FancyTableWrapper build() {
            return fancyTableWrapper;
        }
    }

    public static FancyTableWrapperQuery query() {
        return new FancyTableWrapperQuery();
    }

    public static class FancyTableWrapperQuery extends activejdbc.wrapper.query.AbstractQuery<FancyTableWrapperQuery, FancyTable, FancyTableWrapper> {
        public FancyTableWrapperQuery() {
            super(FancyTable.class, FancyTableWrapper::new);
        }

        protected FancyTableWrapperQuery newQuery() {
            return new FancyTableWrapperQuery();
        }

        public activejdbc.wrapper.query.Condition<FancyTableWrapperQuery, java.lang.Long> fancyTableId() {
            return newCondition("FANCY_TABLE_ID");
        }
    }
}
