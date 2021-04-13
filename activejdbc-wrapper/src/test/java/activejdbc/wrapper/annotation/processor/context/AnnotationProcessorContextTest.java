package activejdbc.wrapper.annotation.processor.context;

import activejdbc.wrapper.annotation.processor.builder.strategy.StrategyHolder;
import activejdbc.wrapper.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;
import org.junit.Test;

public class AnnotationProcessorContextTest {
    AnnotationProcessorContext context = new AnnotationProcessorContext("Suffix");

    @Test
    public void test() {
        StrategyHolder<GetterBuilderStrategy> getterBuilderStrategyHolder = context.getGetterBuilderStrategyHolder();
    }

}