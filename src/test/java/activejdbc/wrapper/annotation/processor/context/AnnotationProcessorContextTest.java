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

package activejdbc.wrapper.annotation.processor.context;

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.*;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter.*;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DataProviderRunner.class)
public class AnnotationProcessorContextTest {

    @DataProvider
    public static Object[][] getter_strategies() {
        return new Object[][]{
                {BigDecimal.class, BigDecimalGetterBuilderStrategy.class},
                {Boolean.class, BooleanGetterBuilderStrategy.class},
                {Clob.class, ClobGetterBuilderStrategy.class},
                {Date.class, DateGetterBuilderStrategy.class},
                {Double.class, DoubleGetterBuilderStrategy.class},
                {Float.class, FloatGetterBuilderStrategy.class},
                {Integer.class, IntegerGetterBuilderStrategy.class},
                {LocalDate.class, LocalDateGetterBuilderStrategy.class},
                {LocalDateTime.class, LocalDateTimeGetterBuilderStrategy.class},
                {LocalTime.class, LocalTimeGetterBuilderStrategy.class},
                {Long.class, LongGetterBuilderStrategy.class},
                {Short.class, ShortGetterBuilderStrategy.class},
                {String.class, StringGetterBuilderStrategy.class},
                {Time.class, TimeGetterBuilderStrategy.class},
                {Timestamp.class, TimestampGetterBuilderStrategy.class},
                {byte[].class, BytesGetterBuilderStrategy.class},
                {java.util.Date.class, UtilDateGetterBuilderStrategy.class},
                {Instant.class, InstantGetterBuilderStrategy.class},
                {BigInteger.class, BigIntegerGetterBuilderStrategy.class},
                {UUID.class, DefaultGetterBuilderStrategy.class},
        };
    }

    @Test
    @UseDataProvider("getter_strategies")
    public void should_provide_getter_strategy(Class<?> type, Class<?> expectedStrategy) {
        // given
        AnnotationProcessorContext context = new AnnotationProcessorContext("Wrapper", "", "");
        // when
        GetterBuilderStrategy strategy = context.getGetterBuilderStrategyHolder().getStrategy(type.getCanonicalName());
        // then
        assertThat(strategy).isExactlyInstanceOf(expectedStrategy);
    }

    @DataProvider
    public static Object[][] setter_strategies() {
        return new Object[][]{
                {Date.class, DateSetterBuilderStrategy.class},
                {LocalDate.class, DateSetterBuilderStrategy.class},
                {LocalDateTime.class, LocalDateTimeSetterBuilderStrategy.class},
                {LocalTime.class, LocalTimeSetterBuilderStrategy.class},
                {Timestamp.class, TimestampSetterBuilderStrategy.class},
                {java.util.Date.class, TimestampSetterBuilderStrategy.class},
                {Instant.class, InstantSetterBuilderStrategy.class},
                {BigInteger.class, BigIntegerSetterBuilderStrategy.class},
                {String.class, DefaultSetterBuilderStrategy.class},
        };
    }

    @Test
    @UseDataProvider("setter_strategies")
    public void should_provide_setter_strategy(Class<?> type, Class<?> expectedStrategy) {
        // given
        AnnotationProcessorContext context = new AnnotationProcessorContext("Wrapper", "", "");
        // when
        SetterBuilderStrategy strategy = context.getSetterBuilderStrategyHolder().getStrategy(type.getCanonicalName());
        // then
        assertThat(strategy).isExactlyInstanceOf(expectedStrategy);
    }

    @Test
    public void should_load_strategies_when_context_class_loader_does_not_see_processor() throws Exception {
        // given: javac and build tools load processors in their own class loader, not in the context one
        Thread thread = Thread.currentThread();
        ClassLoader original = thread.getContextClassLoader();
        try (URLClassLoader isolated = new URLClassLoader(new URL[0], null)) {
            thread.setContextClassLoader(isolated);
            // when
            AnnotationProcessorContext context = new AnnotationProcessorContext("Wrapper", "", "");
            // then
            assertThat(context.getGetterBuilderStrategyHolder().getStrategy(LocalDate.class.getCanonicalName()))
                    .isExactlyInstanceOf(LocalDateGetterBuilderStrategy.class);
            assertThat(context.getSetterBuilderStrategyHolder().getStrategy(LocalDate.class.getCanonicalName()))
                    .isExactlyInstanceOf(DateSetterBuilderStrategy.class);
        } finally {
            thread.setContextClassLoader(original);
        }
    }
}
