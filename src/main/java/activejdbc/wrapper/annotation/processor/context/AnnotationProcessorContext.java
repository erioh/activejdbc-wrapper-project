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

import activejdbc.wrapper.annotation.processor.ActiveJdbcRequiredPropertyWrapperFactory;
import activejdbc.wrapper.annotation.processor.builder.strategy.GetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.builder.strategy.SetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.builder.strategy.StrategyHolder;
import activejdbc.wrapper.annotation.processor.builder.strategy.getter.*;
import activejdbc.wrapper.annotation.processor.builder.strategy.setter.*;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class AnnotationProcessorContext {
    private final StrategyHolder<SetterBuilderStrategy> setterBuilderStrategyHolder;
    private final StrategyHolder<GetterBuilderStrategy> getterBuilderStrategyHolder;
    private final String wrapperSuffix;

    public AnnotationProcessorContext(String wrapperSuffix) {
        this.wrapperSuffix = wrapperSuffix;
        this.getterBuilderStrategyHolder = initGetterBuilderStrategyHolder();
        this.setterBuilderStrategyHolder = initSetterBuilderStrategyHolder();
    }

    private GetterBuilderStrategyHolder initGetterBuilderStrategyHolder() {
        Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies = new HashMap<>();
        getterBuilderStrategies.put(BigDecimal.class, new BigDecimalGetterBuilderStrategy());
        getterBuilderStrategies.put(Boolean.class, new BooleanGetterBuilderStrategy());
        getterBuilderStrategies.put(Clob.class, new ClobGetterBuilderStrategy());
        getterBuilderStrategies.put(Date.class, new DateGetterBuilderStrategy());
        getterBuilderStrategies.put(Double.class, new DoubleGetterBuilderStrategy());
        getterBuilderStrategies.put(Float.class, new FloatGetterBuilderStrategy());
        getterBuilderStrategies.put(Integer.class, new IntegerGetterBuilderStrategy());
        getterBuilderStrategies.put(LocalDate.class, new LocalDateGetterBuilderStrategy());
        getterBuilderStrategies.put(LocalDateTime.class, new LocalDateTimeGetterBuilderStrategy());
        getterBuilderStrategies.put(LocalTime.class, new LocalTimeGetterBuilderStrategy());
        getterBuilderStrategies.put(Long.class, new LongGetterBuilderStrategy());
        getterBuilderStrategies.put(Short.class, new ShortGetterBuilderStrategy());
        getterBuilderStrategies.put(String.class, new StringGetterBuilderStrategy());
        getterBuilderStrategies.put(Time.class, new TimeGetterBuilderStrategy());
        getterBuilderStrategies.put(Timestamp.class, new TimestampGetterBuilderStrategy());
        return new GetterBuilderStrategyHolder(new DefaultGetterBuilderStrategy(), getterBuilderStrategies);
    }

    private SetterBuilderStrategyHolder initSetterBuilderStrategyHolder() {
        Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies = new HashMap<>();
        setterBuilderStrategies.put(Date.class, new DateSetterBuilderStrategy());
        setterBuilderStrategies.put(LocalDate.class, new DateSetterBuilderStrategy());
        setterBuilderStrategies.put(LocalDateTime.class, new LocalDateTimeSetterBuilderStrategy());
        setterBuilderStrategies.put(LocalTime.class, new LocalTimeSetterBuilderStrategy());
        setterBuilderStrategies.put(Timestamp.class, new TimestampSetterBuilderStrategy());
        return new SetterBuilderStrategyHolder(new DefaultSetterBuilderStrategy(), setterBuilderStrategies);
    }

    public ActiveJdbcRequiredPropertyWrapperFactory init() {
        return new ActiveJdbcRequiredPropertyWrapperFactory(this);
    }

    public StrategyHolder<SetterBuilderStrategy> getSetterBuilderStrategyHolder() {
        return setterBuilderStrategyHolder;
    }

    public StrategyHolder<GetterBuilderStrategy> getGetterBuilderStrategyHolder() {
        return getterBuilderStrategyHolder;
    }

    public String getWrapperSuffix() {
        return wrapperSuffix;
    }
}
