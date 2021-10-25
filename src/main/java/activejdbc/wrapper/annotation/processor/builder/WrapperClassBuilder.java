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

package activejdbc.wrapper.annotation.processor.builder;

import activejdbc.wrapper.annotation.processor.ColumnContext;
import activejdbc.wrapper.annotation.processor.builder.strategy.StrategyHolder;
import activejdbc.wrapper.annotation.processor.builder.strategy.WrapperBuilderClassBuilder;
import activejdbc.wrapper.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.setter.SetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.*;

import static activejdbc.wrapper.annotation.processor.util.StringTemplates.*;

public class WrapperClassBuilder {
    /**
     * 1. package name
     * 2. import activejbdc class package
     * 3. activejbdc object class for import
     * 4. wrapper class name
     * 5. activejdbc object class
     * 6  activejdbc object class
     * 7. activejdbc object name
     * 8. constructors
     * 9. methods
     * 10. builder method and builder class
     */
    public static final String CLASS_TEMPLATE = "package %s;%n" +
            "import %s.%s;%n" +
            "public class %s extends activejdbc.wrapper.annotation.processor.builder.ActivejdbcWrapper<%s>{%n" +
            "private %s %s;%n" +
            "%s%n" +
            "%s%n" +
            "%s%n" +
            "}";

    private final StrategyHolder<GetterBuilderStrategy> getterBuilderStrategyHolder;
    private final StrategyHolder<SetterBuilderStrategy> setterBuilderStrategyHolder;
    private final String packageName;
    private final String activejdbcObjectClassName;
    private final String wrapperClassName;
    private final String activejdbcObjectName;
    private final Set<String> settersBody = new HashSet<>();
    private final Set<String> gettersBody = new HashSet<>();
    private final Map<String, String> propertyNamesAndGetters = new HashMap<>();
    private String hashCode = "";
    private String equals = "";
    private String toString = "";
    private String getObject = "";
    private String builderAndBuilderClass = "";

    public WrapperClassBuilder(String packageName, String activejdbcObjectClassName, AnnotationProcessorContext annotationProcessorContext) {
        getterBuilderStrategyHolder = annotationProcessorContext.getGetterBuilderStrategyHolder();
        setterBuilderStrategyHolder = annotationProcessorContext.getSetterBuilderStrategyHolder();
        this.packageName = packageName;
        this.activejdbcObjectClassName = activejdbcObjectClassName;
        this.wrapperClassName = activejdbcObjectClassName + annotationProcessorContext.getWrapperSuffix();
        this.activejdbcObjectName = StringUtils.lowerCaseFirstCharacter(activejdbcObjectClassName);
    }

    public String buildClassBody() {
        StringBuilder methods = new StringBuilder();
        gettersBody.forEach(methods::append);
        settersBody.forEach(methods::append);
        methods.append(getObject)
                .append(toString)
                .append(equals)
                .append(hashCode);

        String constructorWithoutParameters = String.format(CONSTRUCTOR_WITHOUT_PARAMETERS_TEMPLATE, wrapperClassName, activejdbcObjectName, activejdbcObjectClassName);
        String constructorWithParameter = String.format(CONSTRUCTOR_WITH_PARAMETER_TEMPLATE, wrapperClassName, activejdbcObjectClassName, activejdbcObjectName, activejdbcObjectName, activejdbcObjectName);
        String constructors = constructorWithoutParameters + constructorWithParameter;
        return String.format(CLASS_TEMPLATE, packageName, packageName, activejdbcObjectClassName,
                wrapperClassName, activejdbcObjectClassName, activejdbcObjectClassName, activejdbcObjectName, constructors, methods, builderAndBuilderClass);
    }

    public WrapperClassBuilder withHashCode() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (String getter : propertyNamesAndGetters.values()) {
            stringJoiner.add(String.format("this.%s()", getter));
        }
        hashCode = String.format(HASH_CODE_METHOD_TEMPLATE, stringJoiner);
        return this;
    }

    public WrapperClassBuilder withEquals() {
        StringJoiner stringJoiner = new StringJoiner(" && ");
        for (String getter : propertyNamesAndGetters.values()) {
            stringJoiner.add(String.format("%njava.util.Objects.equals(this.%s(), that.%s())", getter, getter));
        }
        equals = String.format(EQUALS_METHOD_TEMPLATE, wrapperClassName, wrapperClassName, stringJoiner);
        return this;
    }

    public WrapperClassBuilder withToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('"')
                .append('{');
        StringJoiner stringJoiner = new StringJoiner(" + \", ");
        propertyNamesAndGetters.forEach((propertyName, getter) ->
                stringJoiner.add(String.format("%s = \" + \"'\" + this.%s() + \"'\"%n", propertyName, getter)));
        stringBuilder.append(stringJoiner);
        stringBuilder.append(" + \"}\"");
        toString = String.format(TO_STRING_METHOD_TEMPLATE, stringBuilder);
        return this;
    }

    public WrapperClassBuilder withGetter(ColumnContext columnContext) {
        String propertyName = StringUtils.isBlank(columnContext.getDesiredFieldName())
                ? StringUtils.buildPropertyNameFromColumnName(columnContext.getColumnName())
                : columnContext.getDesiredFieldName();
        String methodName = StringUtils.buildMethodName(propertyName, "get");
        GetterBuilderStrategy strategy = getterBuilderStrategyHolder.getStrategy(columnContext.getClazz());
        gettersBody.add(strategy.buildGetterBody(columnContext, activejdbcObjectName));
        propertyNamesAndGetters.put(propertyName, methodName);
        return this;
    }

    public WrapperClassBuilder withSetter(ColumnContext columnContext) {
        SetterBuilderStrategy strategy = setterBuilderStrategyHolder.getStrategy(columnContext.getClazz());
        settersBody.add(strategy.buildSetterBody(columnContext, activejdbcObjectName));
        return this;
    }

    public WrapperClassBuilder withMethodGetActivejdbcObject() {
        getObject = String.format(METHOD_GET_OBJECT_TEMPLATE, activejdbcObjectClassName, activejdbcObjectName);
        return this;
    }

    public WrapperClassBuilder withBuilder(List<ColumnContext> columnContexts) {
        StringBuilder stringBuilder = new StringBuilder();
        String builderClassName = wrapperClassName + "Builder";
        stringBuilder.append(String.format(BUILDER_METHOD_TEMPLATE, builderClassName, builderClassName));
        stringBuilder.append(new WrapperBuilderClassBuilder(wrapperClassName, builderClassName, columnContexts).buildClassBody());
        builderAndBuilderClass = stringBuilder.toString();
        return this;
    }
}
