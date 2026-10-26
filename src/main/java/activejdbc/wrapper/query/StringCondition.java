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

package activejdbc.wrapper.query;

/**
 * Condition on a String column: {@link Condition} plus LIKE.
 *
 * @param <Q> the generated query class
 */
public class StringCondition<Q extends AbstractQuery<Q, ?, ?>> extends Condition<Q, String> {

    StringCondition(Q query, String column) {
        super(query, column);
    }

    /**
     * {@code column LIKE pattern}, with SQL wildcards: {@code like("Jo%")}.
     */
    public Q like(String pattern) {
        return compare("LIKE", requireValue(pattern, "like"));
    }

    public Q notLike(String pattern) {
        return compare("NOT LIKE", requireValue(pattern, "notLike"));
    }
}
