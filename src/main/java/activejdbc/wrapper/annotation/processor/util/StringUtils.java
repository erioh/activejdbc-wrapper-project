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

public final class StringUtils {
    public static String lowerCaseFirstCharacter(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static String buildPropertyNameFromColumnName(String columnName) {
        String value = columnName.toLowerCase();
        while (value.contains("_")) {
            String temp = value.replaceFirst("_[a-z,0-9]", String.valueOf(Character.toUpperCase(value.charAt(value.indexOf("_") + 1))));
            if (temp.equals(value)) {
                throw new IllegalArgumentException(String.format("It's not possible to process column name '%s'", columnName));
            }
            value = temp;
        }
        return value;
    }

    public static String buildMethodName(String columnName, String prefix) {
        String propertyName = buildPropertyNameFromColumnName(columnName);
        return prefix + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    private StringUtils() {
    }
}
