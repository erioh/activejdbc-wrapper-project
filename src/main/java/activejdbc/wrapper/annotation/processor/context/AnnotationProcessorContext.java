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
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.GetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.SetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.StrategyHolder;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.*;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;

public class AnnotationProcessorContext {
    private final StrategyHolder<SetterBuilderStrategy> setterBuilderStrategyHolder;
    private final StrategyHolder<GetterBuilderStrategy> getterBuilderStrategyHolder;
    private final String wrapperSuffix;
    private final String builderMethodPrefix;

    public AnnotationProcessorContext(String wrapperSuffix, String builderMethodPrefix) {
        this.wrapperSuffix = wrapperSuffix;
        this.getterBuilderStrategyHolder = initGetterBuilderStrategyHolder();
        this.setterBuilderStrategyHolder = initSetterBuilderStrategyHolder();
        this.builderMethodPrefix = builderMethodPrefix;
    }

    private GetterBuilderStrategyHolder initGetterBuilderStrategyHolder() {
        Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies = loadStrategies(GetterBuilderStrategy.class, GetterBuilderStrategy::typesToApply);
        return new GetterBuilderStrategyHolder(new DefaultGetterBuilderStrategy(), getterBuilderStrategies);
    }

    private SetterBuilderStrategyHolder initSetterBuilderStrategyHolder() {
        Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies = loadStrategies(SetterBuilderStrategy.class, SetterBuilderStrategy::typesToApply);
        return new SetterBuilderStrategyHolder(new DefaultSetterBuilderStrategy(), setterBuilderStrategies);
    }

    /**
     * Strategies are registered in META-INF/services. They must be loaded with the class loader of the processor:
     * javac and build tools load annotation processors in a separate class loader, so the default
     * (thread context) class loader of ServiceLoader doesn't see them.
     */
    private static <T> Map<Class<?>, T> loadStrategies(Class<T> strategyType, Function<T, Set<Class<?>>> typesToApply) {
        Map<Class<?>, T> strategies = new HashMap<>();
        for (T strategy : ServiceLoader.load(strategyType, AnnotationProcessorContext.class.getClassLoader())) {
            for (Class<?> type : typesToApply.apply(strategy)) {
                T previous = strategies.put(type, strategy);
                if (previous != null) {
                    throw new IllegalStateException(String.format("Type %s is claimed by both %s and %s",
                            type.getName(), previous.getClass().getName(), strategy.getClass().getName()));
                }
            }
        }
        return strategies;
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

    public String getBuilderMethodPrefix() {
        return builderMethodPrefix;
    }
}
