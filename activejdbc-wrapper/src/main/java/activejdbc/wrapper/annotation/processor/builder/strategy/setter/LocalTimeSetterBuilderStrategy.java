package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

public class LocalTimeSetterBuilderStrategy implements SetterBuilderStrategy {
    /**
     * 1. setters name
     * 2. setters type
     * 3. property name
     * 4. activejdbc object
     * 5. column name
     * 6. property name
     */
    public static final String SETTER_TEMPLATE = "public void %s(%s %s) {%n" +
            "%s.setTime(\"%s\", java.sql.Time.valueOf(%s));%n" +
            "}%n";

    @Override
    public String getTemplate() {
        return SETTER_TEMPLATE;
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.singleton(LocalTime.class);
    }
}
