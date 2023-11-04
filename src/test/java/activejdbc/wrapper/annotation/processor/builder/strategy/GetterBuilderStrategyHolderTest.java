/*
Copyright 2021-2021 Serhii Demenkov
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package activejdbc.wrapper.annotation.processor.builder.strategy;

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.GetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.DefaultGetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.StringGetterBuilderStrategy;
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