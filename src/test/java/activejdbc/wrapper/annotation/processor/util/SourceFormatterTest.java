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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceFormatterTest {

    @Test
    public void should_indent_by_brace_depth() {
        // given
        String source = lines(
                "public class A {",
                "private B b;",
                "public String getName() {",
                "return b.getString(\"NAME\");",
                "}",
                " public static class Builder {",
                "public A build() {",
                "return a;",
                "}",
                "}",
                "}");
        // when
        String formatted = SourceFormatter.format(source);
        // then
        assertThat(formatted).isEqualTo(lines(
                "public class A {",
                "    private B b;",
                "    public String getName() {",
                "        return b.getString(\"NAME\");",
                "    }",
                "    public static class Builder {",
                "        public A build() {",
                "            return a;",
                "        }",
                "    }",
                "}"));
    }

    @Test
    public void should_ignore_braces_inside_string_and_char_literals() {
        // given: toString emits "{" and "}" inside strings
        String source = lines(
                "public String toString() {",
                "return \"{name = \" + this.getName() + \"}\";",
                "}",
                "char open = '{';",
                "String escaped = \"\\\"{\";");
        // when
        String formatted = SourceFormatter.format(source);
        // then
        assertThat(formatted).isEqualTo(lines(
                "public String toString() {",
                "    return \"{name = \" + this.getName() + \"}\";",
                "}",
                "char open = '{';",
                "String escaped = \"\\\"{\";"));
    }

    @Test
    public void should_indent_continuation_lines() {
        // given
        String source = lines(
                "public LocalDate getDate() {",
                "return java.util.Optional.ofNullable(a.getDate(\"D\"))",
                ".map(java.sql.Date::toLocalDate)",
                ".orElse(null);",
                "}",
                "@Generated(\"x\")",
                "public class A {",
                "}");
        // when
        String formatted = SourceFormatter.format(source);
        // then
        assertThat(formatted).isEqualTo(lines(
                "public LocalDate getDate() {",
                "    return java.util.Optional.ofNullable(a.getDate(\"D\"))",
                "            .map(java.sql.Date::toLocalDate)",
                "            .orElse(null);",
                "}",
                "@Generated(\"x\")",
                "public class A {",
                "}"));
    }

    @Test
    public void should_collapse_repeated_blank_lines() {
        // when
        String formatted = SourceFormatter.format(lines("class A {", "", "", "", "}"));
        // then
        assertThat(formatted).isEqualTo(lines("class A {", "", "}"));
    }

    private static String lines(String... lines) {
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(line).append(System.lineSeparator());
        }
        return result.toString();
    }
}
