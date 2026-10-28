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
package activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter;

import java.util.Collections;
import java.util.Set;

/**
 * Stores an enum by name. Picked for every enum column (ColumnContext.isEnumType()), not looked up by type.
 */
public class EnumSetterBuilderStrategy implements SetterBuilderStrategy {
    /**
     * 1. method name
     * 2. type
     * 3. property name
     * 4. activejdbc object
     * 5. column name
     */
    private static final String SETTER_TEMPLATE = "public void %1$s(%2$s %3$s) {%n" +
            "this.%4$s.set(\"%5$s\", %3$s == null ? null : %3$s.name());%n" +
            "}%n";

    @Override
    public String getTemplate() {
        return SETTER_TEMPLATE;
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.emptySet();
    }
}
