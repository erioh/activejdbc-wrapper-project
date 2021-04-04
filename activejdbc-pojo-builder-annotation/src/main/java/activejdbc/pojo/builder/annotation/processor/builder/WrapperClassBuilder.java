package activejdbc.pojo.builder.annotation.processor.builder;

import activejdbc.pojo.builder.annotation.processor.builder.strategy.GetterBuilderStrategyHolder;
import activejdbc.pojo.builder.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.pojo.builder.annotation.processor.util.StringUtils;

import java.util.*;

import static activejdbc.pojo.builder.annotation.processor.util.StringTemplates.*;

public class WrapperClassBuilder {
    private final GetterBuilderStrategyHolder getterBuilderStrategyHolder;
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
    private String setObject = "";
    private String getObject = "";

    public WrapperClassBuilder(String packageName, String activejdbcObjectClassName, String wrapperSuffix) {
        getterBuilderStrategyHolder = new GetterBuilderStrategyHolder();
        this.packageName = packageName;
        this.activejdbcObjectClassName = activejdbcObjectClassName;
        this.wrapperClassName = activejdbcObjectClassName + wrapperSuffix;
        this.activejdbcObjectName = StringUtils.lowerCaseFirstCharacter(activejdbcObjectClassName);
    }

    public String buildClassBody() {
        StringBuilder methods = new StringBuilder();
        gettersBody.forEach(methods::append);
        settersBody.forEach(methods::append);
        methods.append(setObject)
                .append(getObject)
                .append(toString)
                .append(equals)
                .append(hashCode);
        return String.format(CLASS_TEMPLATE, packageName, packageName, activejdbcObjectClassName,
                wrapperClassName, activejdbcObjectClassName, activejdbcObjectName, activejdbcObjectClassName, methods);
    }

    public WrapperClassBuilder withHashCode() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (String getter : propertyNamesAndGetters.values()) {
            stringJoiner.add(String.format("this.%s()", getter));
        }
        hashCode = String.format(HASH_CODE_METHOD_TRMPLATE, stringJoiner.toString());
        return this;
    }

    public WrapperClassBuilder withEquals() {
        StringJoiner stringJoiner = new StringJoiner(" && ");
        for (String getter : propertyNamesAndGetters.values()) {
            stringJoiner.add(String.format("%njava.util.Objects.equals(this.%s(), that.%s())", getter, getter));
        }
        equals = String.format(EQUALS_METHOD_TEMPLATE, wrapperClassName, wrapperClassName, stringJoiner.toString());
        return this;
    }

    public WrapperClassBuilder withToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('"')
                .append('{');
        StringJoiner stringJoiner = new StringJoiner(" + \", ");
        propertyNamesAndGetters.forEach((propertyName, getter) ->
                stringJoiner.add(String.format("'%s' = \" + this.%s()%n", propertyName, getter)));
        stringBuilder.append(stringJoiner.toString());
        stringBuilder.append(" + \"}\"");
        toString = String.format(TO_STRING_METHOD_TEMPLATE, stringBuilder.toString());
        return this;
    }

    public WrapperClassBuilder withMethodSetActivejdbcObject() {
        setObject = String.format(METHOD_SET_OBJECT_TEMPLATE, activejdbcObjectClassName, activejdbcObjectName, activejdbcObjectName, activejdbcObjectName);
        return this;
    }

    public WrapperClassBuilder withGetter(String type, String columnName) {
        String propertyName = StringUtils.buildPropertyNameFromColumnName(columnName);
        String methodName = StringUtils.buildMethodName(columnName, "get");
        GetterBuilderStrategy getterBuilderStrategy = getterBuilderStrategyHolder.getStrategy(type);
        gettersBody.add(getterBuilderStrategy.buildGetterBody(type, methodName, activejdbcObjectName, columnName));
        propertyNamesAndGetters.put(propertyName, methodName);
        return this;
    }

    public WrapperClassBuilder withSetter(String type, String columnName) {
        String propertyName = StringUtils.buildPropertyNameFromColumnName(columnName);
        String methodName = StringUtils.buildMethodName(columnName, "set");
        settersBody.add(String.format(SETTER_TEMPLATE, methodName, type, propertyName, activejdbcObjectName, columnName, propertyName));
        return this;
    }

    public WrapperClassBuilder withMethodGetActivejdbcObject() {
        getObject = String.format(METHOD_GET_OBJECT_TEMPLATE, activejdbcObjectClassName, activejdbcObjectName);
        return this;
    }
}