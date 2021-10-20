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

import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.Map;

import static activejdbc.wrapper.annotation.processor.util.StringTemplates.BUILDER_SETTER_TEMPLATE;

public class WrapperBuilderClassBuilder {
    /**
     * 1. Builder class name
     * 2. Wrapper class name
     * 3. Wrapper object name
     * 4. Wrapper class name
     * 5. Builder setters
     * 6. Wrapper class name
     * 7. Wrapper object name
     */
    public static final String CLASS_TEMPLATE = " public static class %s {%n" +
            "private final %s %s = new %s();%n" +
            "%s%n" +
            "public %s build() {%n" +
            "return %s;%n" +
            "}%n" +
            "}";
    private final String wrapperClassName;
    private final String builderClassName;
    private final Map<String, String> columnNameWithType;

    public WrapperBuilderClassBuilder(String wrapperClassName, String builderClassName, Map<String, String> columnNameWithType) {
        this.wrapperClassName = wrapperClassName;
        this.builderClassName = builderClassName;
        this.columnNameWithType = columnNameWithType;
    }

    public String buildClassBody() {
        StringBuilder stringBuilder = new StringBuilder();
        String wrapperObjectName = StringUtils.lowerCaseFirstCharacter(wrapperClassName);
        columnNameWithType.forEach((columnName, type) -> {
            String withMethodName = StringUtils.buildMethodName(columnName, "with");
            // todo refactor this thing
            String setMethodName = StringUtils.buildMethodName(columnName, "set");
            String propertyName = StringUtils.buildPropertyNameFromColumnName(columnName);
            stringBuilder.append(String.format(BUILDER_SETTER_TEMPLATE, builderClassName, withMethodName, type, propertyName, wrapperObjectName, setMethodName, propertyName));

        });
        return String.format(CLASS_TEMPLATE, builderClassName, wrapperClassName, wrapperObjectName, wrapperClassName, stringBuilder, wrapperClassName, wrapperObjectName);
    }
}
