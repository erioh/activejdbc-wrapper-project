package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

public class LocalTimeGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. activejdbc object
     * 4. column name
     */
    private static final String GETTER_TEMPLATE = "public %s %s() {%n" +
            "return java.util.Optional.ofNullable(%s.getTime(\"%s\"))%n" +
            ".map(java.sql.Time::toLocalTime)%n" +
            ".orElse(null);%n" +
            "}%n";

    @Override
    public String getTemplate() {
        return GETTER_TEMPLATE;
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.singleton(LocalTime.class);
    }
}
