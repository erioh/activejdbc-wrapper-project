package activejdbc.pojo.builder.annotation.processor.builder.strategy;

import activejdbc.pojo.builder.annotation.processor.builder.strategy.getter.DefaultGetterBuilderStrategy;
import activejdbc.pojo.builder.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class GetterBuilderStrategyHolder {
    private final GetterBuilderStrategy defaultStrategy;
    private final Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies;

    public GetterBuilderStrategyHolder() {
        ServiceLoader<GetterBuilderStrategy> strategies = ServiceLoader.load(
                GetterBuilderStrategy.class,
                this.getClass().getClassLoader()
        );
        this.getterBuilderStrategies = new HashMap<>();
        for (GetterBuilderStrategy strategy : strategies) {
            for (Class<?> clazz : strategy.typesToApply()) {
                getterBuilderStrategies.put(clazz, strategy);
            }
        }
        defaultStrategy = new DefaultGetterBuilderStrategy();
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
