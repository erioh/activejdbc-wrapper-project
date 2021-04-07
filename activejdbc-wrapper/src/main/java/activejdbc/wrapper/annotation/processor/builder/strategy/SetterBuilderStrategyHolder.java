package activejdbc.wrapper.annotation.processor.builder.strategy;

import activejdbc.wrapper.annotation.processor.builder.strategy.setter.DefaultSetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.setter.SetterBuilderStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class SetterBuilderStrategyHolder {
    private final SetterBuilderStrategy defaultStrategy;
    private final Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies;

    public SetterBuilderStrategyHolder() {
        defaultStrategy = new DefaultSetterBuilderStrategy();
        ServiceLoader<SetterBuilderStrategy> strategies = ServiceLoader.load(
                SetterBuilderStrategy.class,
                this.getClass().getClassLoader()
        );
        setterBuilderStrategies = new HashMap<>();
        for (SetterBuilderStrategy strategy : strategies) {
            for (Class<?> clazz : strategy.typesToApply()) {
                setterBuilderStrategies.put(clazz, strategy);
            }
        }
    }

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
