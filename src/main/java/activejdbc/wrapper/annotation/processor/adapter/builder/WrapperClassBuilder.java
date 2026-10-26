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

package activejdbc.wrapper.annotation.processor.adapter.builder;

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.StrategyHolder;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.WrapperBuilderClassBuilder;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter.SetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import activejdbc.wrapper.annotation.processor.util.StringUtils;

import java.util.*;

import static activejdbc.wrapper.annotation.processor.util.StringTemplates.*;

public class WrapperClassBuilder {
    /**
     * 1. package declaration and import of the activejdbc class (empty for the default package)
     * 2. @Generated annotation line (may be empty)
     * 3. wrapper class name
     * 4. activejdbc object class
     * 5. column name constants
     * 6  activejdbc object class
     * 7. activejdbc object name
     * 8. constructors
     * 9. methods
     * 10. builder method and builder class
     */
    public static final String CLASS_TEMPLATE = "%s" +
            "%s" +
            "public class %s extends activejdbc.wrapper.ActivejdbcWrapper<%s>{%n" +
            "%s" +
            "private %s %s;%n" +
            "%s%n" +
            "%s%n" +
            "%s%n" +
            "}";
    /**
     * 1. package name
     * 2. import activejbdc class package
     * 3. activejbdc object class for import
     */
    private static final String PACKAGE_AND_IMPORT_TEMPLATE = "package %s;%n" +
            "import %s.%s;%n";

    private final StrategyHolder<GetterBuilderStrategy> getterBuilderStrategyHolder;
    private final StrategyHolder<SetterBuilderStrategy> setterBuilderStrategyHolder;
    private final String packageName;
    private final String activejdbcObjectClassName;
    private final String wrapperClassName;
    private final String activejdbcObjectName;
    private final Set<String> settersBody = new LinkedHashSet<>();
    private final Set<String> gettersBody = new LinkedHashSet<>();
    private final List<ColumnContext> columnContexts;
    private final Map<String, String> propertyNamesAndGetters = new LinkedHashMap<>();
    private final WrapperBuilderClassBuilder wrapperBuilderClassBuilder;
    private final String generatedAnnotation;
    private String hashCode = StringUtils.EMPTY_STRING;
    private String equals = StringUtils.EMPTY_STRING;
    private String toString = StringUtils.EMPTY_STRING;
    private String getObject = StringUtils.EMPTY_STRING;
    private String wrap = StringUtils.EMPTY_STRING;
    private String builderAndBuilderClass = StringUtils.EMPTY_STRING;

    /**
     * @param activejdbcObjectClassName class name relative to its package: {@code Outer.Inner} for a nested class
     */
    public WrapperClassBuilder(String packageName, String activejdbcObjectClassName, List<ColumnContext> columnContexts, AnnotationProcessorContext annotationProcessorContext) {
        getterBuilderStrategyHolder = annotationProcessorContext.getGetterBuilderStrategyHolder();
        setterBuilderStrategyHolder = annotationProcessorContext.getSetterBuilderStrategyHolder();
        this.packageName = packageName;
        this.activejdbcObjectClassName = activejdbcObjectClassName;
        this.wrapperClassName = wrapperClassName(activejdbcObjectClassName, annotationProcessorContext.getWrapperSuffix());
        this.activejdbcObjectName = StringUtils.lowerCaseFirstCharacter(activejdbcObjectClassName.substring(activejdbcObjectClassName.lastIndexOf('.') + 1));
        this.columnContexts = columnContexts;
        this.wrapperBuilderClassBuilder = new WrapperBuilderClassBuilder(wrapperClassName, annotationProcessorContext, columnContexts);
        this.generatedAnnotation = annotationProcessorContext.getGeneratedAnnotation();
    }

    public String buildClassBody() {
        StringBuilder methods = new StringBuilder();
        gettersBody.forEach(methods::append);
        settersBody.forEach(methods::append);
        methods.append(getObject)
                .append(wrap)
                .append(toString)
                .append(equals)
                .append(hashCode);

        String constructorWithoutParameters = String.format(CONSTRUCTOR_WITHOUT_PARAMETERS_TEMPLATE, wrapperClassName, activejdbcObjectName, activejdbcObjectClassName);
        String constructorWithParameter = String.format(CONSTRUCTOR_WITH_PARAMETER_TEMPLATE, wrapperClassName, activejdbcObjectClassName, activejdbcObjectName, activejdbcObjectName, activejdbcObjectName);
        String constructors = constructorWithoutParameters + constructorWithParameter;
        // a class in the default package can be neither declared in a package nor imported
        String packageAndImport = packageName.isEmpty()
                ? StringUtils.EMPTY_STRING
                : String.format(PACKAGE_AND_IMPORT_TEMPLATE, packageName, packageName, outermostClassName());
        String generatedAnnotationLine = StringUtils.isBlank(generatedAnnotation) ? StringUtils.EMPTY_STRING : String.format("%s%n", generatedAnnotation);
        return String.format(CLASS_TEMPLATE, packageAndImport, generatedAnnotationLine,
                wrapperClassName, activejdbcObjectClassName, buildColumnNameConstants(), activejdbcObjectClassName, activejdbcObjectName, constructors, methods, builderAndBuilderClass);
    }

    private String buildColumnNameConstants() {
        StringBuilder constants = new StringBuilder();
        columnContexts.forEach(columnContext -> constants.append(String.format(COLUMN_NAME_CONSTANT_TEMPLATE,
                StringUtils.buildConstantName(columnContext.getColumnName()), columnContext.getColumnName())));
        return constants.toString();
    }

    /**
     * Wrapper of a nested class {@code Outer.Inner} is {@code Outer_InnerWrapper}, so that nested classes with
     * the same name in different outer classes of one package don't get the same wrapper.
     */
    public static String wrapperClassName(String activejdbcObjectClassName, String wrapperSuffix) {
        return activejdbcObjectClassName.replace('.', '_') + wrapperSuffix;
    }

    // a nested class is referenced as Outer.Inner, so the outermost class is what gets imported
    private String outermostClassName() {
        int firstDot = activejdbcObjectClassName.indexOf('.');
        return firstDot < 0 ? activejdbcObjectClassName : activejdbcObjectClassName.substring(0, firstDot);
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
            stringJoiner.add(String.format("%njava.util.Objects.deepEquals(this.%s(), that.%s())", getter, getter));
        }
        equals = String.format(EQUALS_METHOD_TEMPLATE, wrapperClassName, wrapperClassName, stringJoiner);
        return this;
    }

    public WrapperClassBuilder withToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('"')
                .append('{');
        StringJoiner stringJoiner = new StringJoiner(" + \", ");
        columnContexts.forEach(columnContext -> {
            String propertyName = columnContext.getPropertyName();
            String getter = propertyNamesAndGetters.get(propertyName);
            stringJoiner.add(String.format("%s = \" + (this.%s() == null ? null : \"'\" + %s + \"'\")%n",
                    propertyName, getter, toStringValue(getter, columnContext.getClazz())));
        });
        stringBuilder.append(stringJoiner);
        stringBuilder.append(" + \"}\"");
        toString = String.format(TO_STRING_METHOD_TEMPLATE, stringBuilder);
        return this;
    }

    /**
     * Arrays don't override toString, so "[B@1b6d3586" would be printed. byte[] usually holds BLOB content, which can
     * be large, so only its length is printed.
     */
    private static String toStringValue(String getter, String type) {
        if ("byte[]".equals(type)) {
            return String.format("\"<\" + this.%s().length + \" bytes>\"", getter);
        }
        if (type.endsWith("[][]")) {
            return String.format("java.util.Arrays.deepToString(this.%s())", getter);
        }
        if (type.endsWith("[]")) {
            return String.format("java.util.Arrays.toString(this.%s())", getter);
        }
        return String.format("this.%s()", getter);
    }

    public WrapperClassBuilder withGetters() {
        this.columnContexts.forEach(columnContext -> {
            String propertyName = columnContext.getPropertyName();
            String methodName = StringUtils.buildMethodName(propertyName, "get");
            GetterBuilderStrategy strategy = getterBuilderStrategyHolder.getStrategy(columnContext.getClazz());
            gettersBody.add(strategy.buildGetterBody(columnContext, activejdbcObjectName));
            propertyNamesAndGetters.put(propertyName, methodName);
        });
        return this;
    }

    public WrapperClassBuilder withSetters() {
        this.columnContexts.forEach(columnContext -> {
            SetterBuilderStrategy strategy = setterBuilderStrategyHolder.getStrategy(columnContext.getClazz());
            settersBody.add(strategy.buildSetterBody(columnContext, activejdbcObjectName));
        });
        return this;
    }

    public WrapperClassBuilder withWrapMethod() {
        wrap = String.format(WRAP_METHOD_TEMPLATE, wrapperClassName, activejdbcObjectClassName, wrapperClassName, activejdbcObjectClassName, wrapperClassName);
        return this;
    }

    public WrapperClassBuilder withMethodGetActivejdbcObject() {
        getObject = String.format(METHOD_GET_OBJECT_TEMPLATE, activejdbcObjectClassName, activejdbcObjectName);
        return this;
    }

    public WrapperClassBuilder withBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(wrapperBuilderClassBuilder.buildBuildMethodName());
        stringBuilder.append(wrapperBuilderClassBuilder.buildClassBody());
        builderAndBuilderClass = stringBuilder.toString();
        return this;
    }
}
