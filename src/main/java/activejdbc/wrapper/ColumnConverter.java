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
package activejdbc.wrapper;

/**
 * Converts a property between its Java type and the type stored in the column, for types the wrapper doesn't
 * support by itself: value objects, JSON, enums stored by code. Named in
 * {@code @ActiveJdbcRequiredProperty(converter = ...)}; the column is then read and written as {@code D} and the
 * property has type {@code T}, which must be the annotation's {@code clazz}.
 * <p>
 * The wrapper creates one instance per column with the public constructor without parameters and shares it, so a
 * converter must be stateless or thread-safe. It is never called with {@code null}: NULL in the column is
 * {@code null} in the property and the other way round. The query builder converts its parameters with
 * {@link #toDatabase(Object)} too.
 *
 * <pre>{@code
 * public class MoneyConverter implements ColumnConverter<Money, BigDecimal> {
 *     public BigDecimal toDatabase(Money value) { return value.getAmount(); }
 *     public Money fromDatabase(BigDecimal value) { return new Money(value); }
 * }
 * }</pre>
 *
 * @param <T> type of the property
 * @param <D> type of the column value, one the wrapper supports, e.g. {@code String}, {@code BigDecimal},
 *            {@code LocalDate}
 */
public interface ColumnConverter<T, D> {

    /**
     * Converts a property value for the column.
     *
     * @param value the property value, not null
     * @return the value to store in the column
     */
    D toDatabase(T value);

    /**
     * Converts a value read from the column for the property.
     *
     * @param value the value read from the column, not null
     * @return the property value
     */
    T fromDatabase(D value);
}
