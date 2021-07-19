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
            "return java.util.Objects.hash(%s);%n" +
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
}
