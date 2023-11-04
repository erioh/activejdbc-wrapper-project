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

package activejdbc.wrapper.annotation.processor.adapter.builder.strategy;

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter.SetterBuilderStrategy;

import java.util.Map;

public class SetterBuilderStrategyHolder implements StrategyHolder<SetterBuilderStrategy> {
    private final SetterBuilderStrategy defaultStrategy;
    private final Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies;

    public SetterBuilderStrategyHolder(SetterBuilderStrategy defaultStrategy, Map<Class<?>, SetterBuilderStrategy> setterBuilderStrategies) {

        this.defaultStrategy = defaultStrategy;
        this.setterBuilderStrategies = setterBuilderStrategies;
    }

    @Override
    public SetterBuilderStrategy getStrategy(String type) {
        return setterBuilderStrategies.getOrDefault(getClass(type), defaultStrategy);
    }

    private Class<?> getClass(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unknown type " + type, e);
        }
    }
}
