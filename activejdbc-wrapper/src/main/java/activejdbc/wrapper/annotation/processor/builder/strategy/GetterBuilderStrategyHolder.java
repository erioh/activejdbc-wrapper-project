package activejdbc.wrapper.annotation.processor.builder.strategy;

import activejdbc.wrapper.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;

import java.util.Map;

public class GetterBuilderStrategyHolder implements StrategyHolder<GetterBuilderStrategy>{
    private final GetterBuilderStrategy defaultStrategy;
    private final Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies;

    public GetterBuilderStrategyHolder(GetterBuilderStrategy defaultStrategy, Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies) {
        this.defaultStrategy = defaultStrategy;

        this.getterBuilderStrategies = getterBuilderStrategies;
    }

    @Override
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
