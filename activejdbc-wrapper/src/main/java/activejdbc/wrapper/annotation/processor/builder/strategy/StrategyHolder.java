package activejdbc.wrapper.annotation.processor.builder.strategy;

public interface StrategyHolder<T> {
    T getStrategy(String type);
}
