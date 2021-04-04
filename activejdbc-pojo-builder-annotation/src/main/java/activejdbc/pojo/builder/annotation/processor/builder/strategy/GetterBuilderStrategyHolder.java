package activejdbc.pojo.builder.annotation.processor.builder.strategy;

import activejdbc.pojo.builder.annotation.processor.builder.strategy.getter.DefaultGetterBuilderStrategy;
import activejdbc.pojo.builder.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.pojo.builder.annotation.processor.builder.strategy.getter.LocalDateGetterBuilderStrategy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class GetterBuilderStrategyHolder {
    private final GetterBuilderStrategy defaultStrategy;
    private final Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies;

    public GetterBuilderStrategyHolder() {
        defaultStrategy = new DefaultGetterBuilderStrategy();
        getterBuilderStrategies = new HashMap<>();
        getterBuilderStrategies.put(LocalDate.class, new LocalDateGetterBuilderStrategy());
    }

    public GetterBuilderStrategy getStrategy(String type) {
        return getterBuilderStrategies.getOrDefault(getClass(type), defaultStrategy);
    }

    private Class<?> getClass(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unknown type " + type, e);
        }
    }
}
