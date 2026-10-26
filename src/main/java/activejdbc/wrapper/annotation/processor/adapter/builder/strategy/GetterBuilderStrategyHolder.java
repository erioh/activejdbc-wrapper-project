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

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.GetterBuilderStrategy;

import java.util.HashMap;
import java.util.Map;

public class GetterBuilderStrategyHolder implements StrategyHolder<GetterBuilderStrategy> {
    private final GetterBuilderStrategy defaultStrategy;
    /**
     * Keyed by canonical type name: the type comes from the code being compiled, so it may be primitive, an array
     * or a class that is not on the processor classpath, and must not be loaded with Class.forName.
     */
    private final Map<String, GetterBuilderStrategy> getterBuilderStrategies = new HashMap<>();

    public GetterBuilderStrategyHolder(GetterBuilderStrategy defaultStrategy, Map<Class<?>, GetterBuilderStrategy> getterBuilderStrategies) {
        this.defaultStrategy = defaultStrategy;
        getterBuilderStrategies.forEach((type, strategy) -> this.getterBuilderStrategies.put(type.getCanonicalName(), strategy));
    }

    @Override
    public GetterBuilderStrategy getStrategy(String type) {
        return getterBuilderStrategies.getOrDefault(type, defaultStrategy);
    }
}
