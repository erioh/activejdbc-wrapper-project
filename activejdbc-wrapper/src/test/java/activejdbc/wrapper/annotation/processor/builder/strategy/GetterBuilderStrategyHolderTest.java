package activejdbc.wrapper.annotation.processor.builder.strategy;

import activejdbc.wrapper.annotation.processor.builder.strategy.getter.DefaultGetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.getter.StringGetterBuilderStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GetterBuilderStrategyHolderTest {
    private GetterBuilderStrategyHolder strategyHolder;

    @Before
    public void setUp() {
        GetterBuilderStrategy defaultGetterBuilderStrategy = new DefaultGetterBuilderStrategy();
        GetterBuilderStrategy realGetterBuilderStrategy = new StringGetterBuilderStrategy();
        strategyHolder = new GetterBuilderStrategyHolder(defaultGetterBuilderStrategy
                , Collections.singletonMap(String.class, realGetterBuilderStrategy));
    }

    @Test
    public void should_return_real_strategy() {
        // when
        GetterBuilderStrategy strategy = strategyHolder.getStrategy(String.class.getName());
        // then
        assertThat(strategy)
                .isInstanceOf(StringGetterBuilderStrategy.class);
    }

    @Test
    public void should_return_default_strategy() {
        // when
        GetterBuilderStrategy strategy = strategyHolder.getStrategy(Object.class.getName());
        // then
        assertThat(strategy)
                .isInstanceOf(DefaultGetterBuilderStrategy.class);
    }
}