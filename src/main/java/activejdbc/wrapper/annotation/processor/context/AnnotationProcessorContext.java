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
import activejdbc.wrapper.annotation.processor.builder.strategy.getter.DefaultGetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.setter.DefaultSetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.setter.SetterBuilderStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

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
        ServiceLoader<GetterBuilderStrategy> strategies = ServiceLoader.load(GetterBuilderStrategy.class);
        Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies = new HashMap<>();
        for (GetterBuilderStrategy strategy : strategies) {
            for (Class<?> clazz : strategy.typesToApply()) {
                getterBuilderStrategies.put(clazz, strategy);
            }
        }
        return new GetterBuilderStrategyHolder(new DefaultGetterBuilderStrategy(), getterBuilderStrategies);
    }

    private SetterBuilderStrategyHolder initSetterBuilderStrategyHolder() {
        ServiceLoader<SetterBuilderStrategy> strategies = ServiceLoader.load(SetterBuilderStrategy.class);
        Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies = new HashMap<>();
        for (SetterBuilderStrategy strategy : strategies) {
            for (Class<?> clazz : strategy.typesToApply()) {
                setterBuilderStrategies.put(clazz, strategy);
            }
        }
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
