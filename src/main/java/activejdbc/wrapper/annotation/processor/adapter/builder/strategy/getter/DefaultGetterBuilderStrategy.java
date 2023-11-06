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

import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.Collections;
import java.util.Set;

public class DefaultGetterBuilderStrategy implements GetterBuilderStrategy {
    /**
     * 1. return type
     * 2. method name
     * 3. class for casting
     * 4. activejdbc object
     * 5. column name
     */
    private static final String GETTER_TEMPLATE = "public %s %s() {%n" +
            "return (%s) %s.get(\"%s\");%n" +
            "}%n";

    @Override
    public String buildGetterBody(ColumnContext columnContext, String activejdbcObjectName) {
        String propertyName = StringUtils.isBlank(columnContext.getDesiredFieldName())
                ? StringUtils.buildPropertyNameFromColumnName(columnContext.getColumnName())
                : columnContext.getDesiredFieldName();
        String methodName = StringUtils.buildMethodName(propertyName, "get");
        return String.format(getTemplate(), columnContext.getClazz(), methodName, columnContext.getClazz(), activejdbcObjectName, columnContext.getColumnName());
    }

    @Override
    public String getTemplate() {
        return GETTER_TEMPLATE;
    }

    @Override
    public Set<Class<?>> typesToApply() {
        return Collections.emptySet();
    }
}