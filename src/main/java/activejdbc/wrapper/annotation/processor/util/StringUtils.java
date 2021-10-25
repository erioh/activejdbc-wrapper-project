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

import java.util.Arrays;
import java.util.stream.Collectors;

public final class StringUtils {

    public static boolean isBlank(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static boolean isValid(String string) {
        return string.trim().matches("^[a-zA-Z0-9_]+$");
    }

    public static String lowerCaseFirstCharacter(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static String buildPropertyNameFromColumnName(String columnName) {
        String value = columnName.toLowerCase();
        String[] strings = value.split("_");
        return changeCapitalizationOfTheFirstCharacter(Arrays.stream(strings)
                .filter(string -> !isBlank(string))
                .map(string -> changeCapitalizationOfTheFirstCharacter(string, true))
                .collect(Collectors.joining()), false);
    }

    private static String changeCapitalizationOfTheFirstCharacter(String string, boolean toUppercase) {
        String firstCharacter = string.substring(0, 1);
        String allOtherCharacters = string.substring(1);
        return toUppercase ? firstCharacter.toUpperCase() + allOtherCharacters : firstCharacter.toLowerCase() + allOtherCharacters;
    }

    public static String buildMethodName(String propertyName, String prefix) {
        return prefix + changeCapitalizationOfTheFirstCharacter(propertyName, true);
    }

    private StringUtils() {
    }
}
