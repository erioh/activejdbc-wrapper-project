package activejdbc.pojo.builder.annotation.processor.builder.strategy;

import activejdbc.pojo.builder.annotation.processor.builder.strategy.setter.DateSetterBuilderStrategy;
import activejdbc.pojo.builder.annotation.processor.builder.strategy.setter.DefaultSetterBuilderStrategy;
import activejdbc.pojo.builder.annotation.processor.builder.strategy.setter.SetterBuilderStrategy;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SetterBuilderStrategyHolder {
    private final SetterBuilderStrategy defaultStrategy;
    private final Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies;

    public SetterBuilderStrategyHolder() {
        defaultStrategy = new DefaultSetterBuilderStrategy();
        setterBuilderStrategies = new HashMap<>();
        setterBuilderStrategies.put(Timestamp.class, new DateSetterBuilderStrategy());
//        setterBuilderStrategies.put(LocalDateTime.class, new TimestampSetterBuilderStrategy());
        setterBuilderStrategies.put(LocalDate.class, new DateSetterBuilderStrategy());
//        setterBuilderStrategies.put(LocalTime.class, new TimestampSetterBuilderStrategy());
        setterBuilderStrategies.put(Date.class, new DateSetterBuilderStrategy());

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
