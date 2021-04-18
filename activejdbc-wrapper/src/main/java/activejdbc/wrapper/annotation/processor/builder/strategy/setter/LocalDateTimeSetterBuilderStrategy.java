package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

public class LocalDateTimeSetterBuilderStrategy implements SetterBuilderStrategy {
    /**
     * 1. setters name
     * 2. setters type
     * 3. property name
     * 4. activejdbc object
     * 5. column name
     * 6. property name
     */
    public static final String SETTER_TEMPLATE = "public void %s(%s %s) {%n" +
            "%s.setTimestamp(\"%s\", java.sql.Timestamp.valueOf(%s));%n" +
            "}%n";

    @Override
    public String getTemplate() {
        return SETTER_TEMPLATE;
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.singleton(LocalDateTime.class);
    }
}
