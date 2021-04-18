package activejdbc.wrapper.annotation.processor.builder;

import org.javalite.activejdbc.Model;

public abstract class ActivejdbcWrapper<T extends Model> {
    protected abstract T getActivejdbcObject();
}
