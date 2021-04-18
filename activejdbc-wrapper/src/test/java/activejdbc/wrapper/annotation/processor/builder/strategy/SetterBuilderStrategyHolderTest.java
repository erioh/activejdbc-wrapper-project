package activejdbc.wrapper.annotation.processor.builder.strategy;

import activejdbc.wrapper.annotation.processor.builder.strategy.setter.DateSetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.setter.DefaultSetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.setter.SetterBuilderStrategy;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SetterBuilderStrategyHolderTest {
    private SetterBuilderStrategyHolder strategyHolder;

    @Before
    public void setUp() {
        SetterBuilderStrategy defaultGetterBuilderStrategy = new DefaultSetterBuilderStrategy();
        SetterBuilderStrategy realGetterBuilderStrategy = new DateSetterBuilderStrategy();
        strategyHolder = new SetterBuilderStrategyHolder(defaultGetterBuilderStrategy
                , Collections.singletonMap(Date.class, realGetterBuilderStrategy));
    }

    @Test
    public void should_return_real_strategy() {
        // when
        SetterBuilderStrategy strategy = strategyHolder.getStrategy(Date.class.getName());
        // then
        assertThat(strategy)
                .isInstanceOf(DateSetterBuilderStrategy.class);
    }

    @Test
    public void should_return_default_strategy() {
        // when
        SetterBuilderStrategy strategy = strategyHolder.getStrategy(Object.class.getName());
        // then
        assertThat(strategy)
                .isInstanceOf(DefaultSetterBuilderStrategy.class);
    }
}