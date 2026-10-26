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

/**
 * Indents generated source by brace depth, so wrappers are readable in an IDE or debugger. Templates emit every line
 * flush left; this only adds indentation and drops repeated blank lines, it never changes code.
 */
public final class SourceFormatter {
    private static final String INDENT = "    ";
    private static final String CONTINUATION_INDENT = INDENT + INDENT;

    public static String format(String source) {
        StringBuilder result = new StringBuilder();
        int depth = 0;
        boolean continuation = false;
        boolean pendingBlankLine = false;
        for (String rawLine : source.split("\\R", -1)) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                // written only before the next non-blank line: collapses repeated blank lines and drops trailing ones
                pendingBlankLine = result.length() > 0;
                continue;
            }
            if (pendingBlankLine) {
                result.append(System.lineSeparator());
                pendingBlankLine = false;
            }
            int lineDepth = line.startsWith("}") ? depth - 1 : depth;
            for (int i = 0; i < lineDepth; i++) {
                result.append(INDENT);
            }
            if (continuation) {
                result.append(CONTINUATION_INDENT);
            }
            result.append(line).append(System.lineSeparator());
            depth += braceBalance(line);
            // a statement that doesn't end on this line continues on the next one (chained calls, &&, +)
            continuation = !(line.endsWith(";") || line.endsWith("{") || line.endsWith("}") || line.startsWith("@"));
        }
        return result.toString();
    }

    /**
     * Opening minus closing braces, ignoring the ones inside string and char literals (toString emits "{" and "}").
     */
    private static int braceBalance(String line) {
        int balance = 0;
        char quote = 0;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (quote != 0) {
                if (character == '\\') {
                    i++;
                } else if (character == quote) {
                    quote = 0;
                }
            } else if (character == '"' || character == '\'') {
                quote = character;
            } else if (character == '{') {
                balance++;
            } else if (character == '}') {
                balance--;
            }
        }
        return balance;
    }

    private SourceFormatter() {
    }
}
