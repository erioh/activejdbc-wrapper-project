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

package activejdbc.wrapper.annotation.processor.util;

public final class StringTemplates {

    public static final String METHOD_GET_OBJECT_TEMPLATE = "protected %s getActivejdbcObject() {%n" +
            "return %s;%n" +
            "}%n";
    /**
     * 1. wrapper class name
     * 2. activejdbc object class
     * 3. wrapper class name
     * 4. activejdbc object class
     * 5. wrapper class name
     */
    public static final String WRAP_METHOD_TEMPLATE = "public static java.util.List<%s> wrap(java.util.Collection<? extends %s> models) {%n" +
            "java.util.List<%s> wrappers = new java.util.ArrayList<>(models.size());%n" +
            "for (%s model : models) {%n" +
            "wrappers.add(new %s(model));%n" +
            "}%n" +
            "return wrappers;%n" +
            "}%n";
    /**
     * 1. constant name
     * 2. column name
     */
    public static final String COLUMN_NAME_CONSTANT_TEMPLATE = "public static final java.lang.String %s = \"%s\";%n";
    /**
     * 1. converter class
     * 2. field name
     * 3. converter class
     */
    public static final String CONVERTER_FIELD_TEMPLATE = "private static final %s %s = new %s();%n";
    /**
     * 1. property type
     * 2. getter name
     * 3. database type
     * 4. read method name
     * 5. converter field name
     */
    public static final String CONVERTING_GETTER_TEMPLATE = "public %s %s() {%n" +
            "%s value = this.%s();%n" +
            "return value == null ? null : %s.fromDatabase(value);%n" +
            "}%n";
    /**
     * 1. setter name
     * 2. property type
     * 3. property name
     * 4. write method name
     * 5. property name
     * 6. converter field name
     * 7. property name
     */
    public static final String CONVERTING_SETTER_TEMPLATE = "public void %s(%s %s) {%n" +
            "this.%s(%s == null ? null : %s.toDatabase(%s));%n" +
            "}%n";
    /**
     * 1. toString implementation
     */
    public static final String TO_STRING_METHOD_TEMPLATE = "public java.lang.String toString() {%n" +
            "return %s;%n" +
            "}%n";

    /**
     * 1. wrapper class name
     * 2. wrapper class name
     * 3. generated checks for equals
     */
    public static final String EQUALS_METHOD_TEMPLATE = "public boolean equals(Object o) {%n" +
            "if (this == o) return true;%n" +
            "if (o == null || getClass() != o.getClass()) return false;%n" +
            "%s that = (%s) o;%n" +
            "return%s;%n" +
            "}%n";
    /**
     * 1. generated call of getters
     */
    public static final String HASH_CODE_METHOD_TEMPLATE = "public int hashCode() {%n" +
            "return java.util.Arrays.deepHashCode(new java.lang.Object[]{%s});%n" +
            "}%n";

    /**
     * 1. Wrapper name
     * 2. activejdbc object name
     * 3. activejdbc object class
     */
    public static final String CONSTRUCTOR_WITHOUT_PARAMETERS_TEMPLATE = " public %s() {%n" +
            "this.%s = new %s();%n" +
            "}%n";

    /**
     * 1. Wrapper name
     * 2. activejdbc object class
     * 2. activejdbc object name
     * 3. activejdbc object name
     * 4. activejdbc object name
     */
    public static final String CONSTRUCTOR_WITH_PARAMETER_TEMPLATE = " public %s(%s %s) {%n" +
            "this.%s = %s;%n" +
            "}%n";
    /**
     * 1. Wrapper class name
     */
    public static final String BUILDER_METHOD_TEMPLATE = " public static %s builder() {%n" +
            "return new %s();%n" +
            "}%n";

    /**
     * 1. Builder class name
     * 2. Builder class name
     * 3. builder method calls copying the properties, each starting on a new line
     */
    public static final String TO_BUILDER_METHOD_TEMPLATE = " public %s toBuilder() {%n" +
            "return new %s()%s;%n" +
            "}%n";

    /**
     * 1. Builder class name
     * 2. Method name
     * 3. Column type
     * 4. Column name
     * 5. Activejdbc wrapper object name
     * 6. Setters name
     * 7. Column name
     */
    public static final String BUILDER_SETTER_TEMPLATE = " public %s %s(%s %s) {%n" +
            "this.%s.%s(%s);%n" +
            "return this;%n" +
            "}%n";
}
