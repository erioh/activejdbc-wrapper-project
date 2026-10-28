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

import activejdbc.wrapper.annotation.processor.context.ColumnContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates the static {@code query()} method and the nested query class of a wrapper, a typed front end for
 * {@code activejdbc.wrapper.query.AbstractQuery}.
 */
public class WrapperQueryClassBuilder {
    /**
     * Column methods take no parameters, so a property with one of these names would clash with a method of
     * AbstractQuery or Object. Such columns are left out of the query class.
     */
    public static final Set<String> RESERVED_METHOD_NAMES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "and", "or", "group", "limit", "offset", "fetch", "fetchModels", "fetchFirst", "fetchOne", "exists",
            "fetchCount", "getSql", "getOrderBy", "getParams", "newCondition", "newStringCondition", "newQuery",
            "getClass", "hashCode", "toString", "clone", "finalize", "wait", "notify", "notifyAll")));
    /**
     * 1. Query class name
     * 2. Query class name
     */
    private static final String QUERY_METHOD_TEMPLATE = "public static %s query() {%n" +
            "return new %s();%n" +
            "}%n";
    /**
     * 1. Query class name
     * 2. Query class name
     * 3. activejdbc object class
     * 4. Wrapper class name
     * 5. Query class name
     * 6. activejdbc object class
     * 7. Wrapper class name
     * 8. Query class name
     * 9. Query class name
     * 10. column methods
     */
    private static final String CLASS_TEMPLATE = "public static class %s extends activejdbc.wrapper.query.AbstractQuery<%s, %s, %s> {%n" +
            "public %s() {%n" +
            "super(%s.class, %s::new);%n" +
            "}%n" +
            "protected %s newQuery() {%n" +
            "return new %s();%n" +
            "}%n" +
            "%s" +
            "}%n";
    /**
     * 1. condition type
     * 2. property name
     * 3. factory method
     * 4. column name
     */
    private static final String COLUMN_METHOD_TEMPLATE = "public %s %s() {%n" +
            "return %s(\"%s\");%n" +
            "}%n";

    private final String wrapperClassName;
    private final String activejdbcObjectClassName;
    private final String queryClassName;
    private final List<ColumnContext> columnContexts;

    public WrapperQueryClassBuilder(String wrapperClassName, String activejdbcObjectClassName, List<ColumnContext> columnContexts) {
        this.wrapperClassName = wrapperClassName;
        this.activejdbcObjectClassName = activejdbcObjectClassName;
        this.queryClassName = wrapperClassName + "Query";
        this.columnContexts = columnContexts;
    }

    /**
     * @return columns that get no method in the query class, see {@link #RESERVED_METHOD_NAMES}
     */
    public static List<ColumnContext> columnsLeftOut(List<ColumnContext> columnContexts) {
        return columnContexts.stream()
                .filter(columnContext -> RESERVED_METHOD_NAMES.contains(columnContext.getPropertyName()))
                .collect(Collectors.toList());
    }

    public String buildQueryMethod() {
        return String.format(QUERY_METHOD_TEMPLATE, queryClassName, queryClassName);
    }

    public String buildClassBody() {
        StringBuilder columnMethods = new StringBuilder();
        columnContexts.stream()
                .filter(columnContext -> !RESERVED_METHOD_NAMES.contains(columnContext.getPropertyName()))
                .forEach(columnContext -> {
                    boolean string = "java.lang.String".equals(columnContext.getClazz()) || "String".equals(columnContext.getClazz());
                    String conditionType = string
                            ? String.format("activejdbc.wrapper.query.StringCondition<%s>", queryClassName)
                            : String.format("activejdbc.wrapper.query.Condition<%s, %s>", queryClassName, columnContext.getClazz());
                    columnMethods.append(String.format(COLUMN_METHOD_TEMPLATE, conditionType, columnContext.getPropertyName(),
                            string ? "newStringCondition" : "newCondition", columnContext.getColumnName()));
                });
        return String.format(CLASS_TEMPLATE, queryClassName, queryClassName, activejdbcObjectClassName, wrapperClassName,
                queryClassName, activejdbcObjectClassName, wrapperClassName, queryClassName, queryClassName, columnMethods);
    }
}
