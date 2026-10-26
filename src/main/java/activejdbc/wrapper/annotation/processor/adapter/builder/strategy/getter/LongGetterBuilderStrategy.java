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

package activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter;

import java.util.Collections;
import java.util.Set;

public class LongGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. activejdbc object
     * 4. column name
     */
    private static final String GETTER_TEMPLATE = "public %s %s() {%n" +
            "return %s.getLong(\"%s\");%n" +
            "}%n";

    @Override
    public String getTemplate() {
        return GETTER_TEMPLATE;
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.singleton(Long.class);
    }
}
