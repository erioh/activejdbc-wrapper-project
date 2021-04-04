package activejdbc.pojo.builder.annotation.processor.builder.strategy;

import activejdbc.pojo.builder.annotation.processor.builder.strategy.getter.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class GetterBuilderStrategyHolder {
    private final GetterBuilderStrategy defaultStrategy;
    private final Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies;

    public GetterBuilderStrategyHolder() {
        defaultStrategy = new DefaultGetterBuilderStrategy();
        getterBuilderStrategies = new HashMap<>();
        getterBuilderStrategies.put(LocalDate.class, new LocalDateGetterBuilderStrategy());
        getterBuilderStrategies.put(LocalDateTime.class, new LocalDateTimeGetterBuilderStrategy());
        getterBuilderStrategies.put(LocalTime.class, new LocalTimeGetterBuilderStrategy());
        getterBuilderStrategies.put(Boolean.class, new BooleanGetterBuilderStrategy());
        getterBuilderStrategies.putIfAbsent(Timestamp.class, new TimestampGetterBuilderStrategy());
        getterBuilderStrategies.putIfAbsent(Float.class, new FloatGetterBuilderStrategy());
        getterBuilderStrategies.putIfAbsent(Integer.class, new IntegerGetterBuilderStrategy());
        getterBuilderStrategies.putIfAbsent(Double.class, new DoubleGetterBuilderStrategy());
        getterBuilderStrategies.putIfAbsent(Long.class, new LongGetterBuilderStrategy());
        getterBuilderStrategies.putIfAbsent(Time.class, new TimeGetterBuilderStrategy());
        getterBuilderStrategies.putIfAbsent(Short.class, new ShortGetterBuilderStrategy());
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
