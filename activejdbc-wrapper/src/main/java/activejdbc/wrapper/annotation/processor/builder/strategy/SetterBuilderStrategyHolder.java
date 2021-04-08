package activejdbc.wrapper.annotation.processor.builder.strategy;

import activejdbc.wrapper.annotation.processor.builder.strategy.setter.SetterBuilderStrategy;

import java.util.Map;

public class SetterBuilderStrategyHolder implements StrategyHolder<SetterBuilderStrategy> {
    private final SetterBuilderStrategy defaultStrategy;
    private final Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies;

    public SetterBuilderStrategyHolder(SetterBuilderStrategy defaultStrategy, Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies) {

        this.defaultStrategy = defaultStrategy;
        this.setterBuilderStrategies = setterBuilderStrategies;
    }

    @Override
    public SetterBuilderStrategy getStrategy(String type) {
        return setterBuilderStrategies.getOrDefault(getClass(type), defaultStrategy);
    }

    private Class<?> getClass(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unknown type " + type, e);
        }
    }
}
