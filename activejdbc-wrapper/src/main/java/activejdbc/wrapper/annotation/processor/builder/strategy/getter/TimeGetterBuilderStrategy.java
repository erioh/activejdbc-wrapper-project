package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import java.sql.Time;
import java.util.Collections;
import java.util.Set;

public class TimeGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. activejdbc object
     * 4. column name
     */
    private static final String GETTER_TEMPLATE = "public %s %s() {%n" +
            "return new java.sql.Time(%s.getDate(\"%s\").getTime());%n" +
            "}%n";

    @Override
    public String getTemplate() {
        return GETTER_TEMPLATE;
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.singleton(Time.class);
    }
}
