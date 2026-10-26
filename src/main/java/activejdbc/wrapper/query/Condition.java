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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Condition on one column of a generated query; every operator adds the condition and returns the query.
 *
 * @param <Q> the generated query class
 * @param <T> type of the column
 */
public class Condition<Q extends AbstractQuery<Q, ?, ?>, T> {
    private final Q query;
    private final String column;

    Condition(Q query, String column) {
        this.query = query;
        this.column = column;
    }

    /**
     * {@code column = value}; {@code eq(null)} means {@code IS NULL}, since {@code = NULL} never matches in SQL.
     */
    public Q eq(T value) {
        return value == null ? isNull() : compare("=", value);
    }

    /**
     * {@code column <> value}; {@code ne(null)} means {@code IS NOT NULL}.
     */
    public Q ne(T value) {
        return value == null ? isNotNull() : compare("<>", value);
    }

    public Q gt(T value) {
        return compare(">", requireValue(value, "gt"));
    }

    public Q ge(T value) {
        return compare(">=", requireValue(value, "ge"));
    }

    public Q lt(T value) {
        return compare("<", requireValue(value, "lt"));
    }

    public Q le(T value) {
        return compare("<=", requireValue(value, "le"));
    }

    /**
     * {@code column BETWEEN from AND to}, both ends included.
     */
    public Q between(T from, T to) {
        return query.append(column + " BETWEEN ? AND ?",
                Arrays.asList(toJdbc(requireValue(from, "between")), toJdbc(requireValue(to, "between"))));
    }

    @SafeVarargs
    public final Q in(T... values) {
        return in(Arrays.asList(values));
    }

    /**
     * {@code column IN (...)}; an empty collection matches nothing.
     */
    public Q in(Collection<? extends T> values) {
        return values.isEmpty() ? query.append("1 = 0", Collections.emptyList()) : list("IN", values);
    }

    @SafeVarargs
    public final Q notIn(T... values) {
        return notIn(Arrays.asList(values));
    }

    /**
     * {@code column NOT IN (...)}; an empty collection matches everything.
     */
    public Q notIn(Collection<? extends T> values) {
        return values.isEmpty() ? query.append("1 = 1", Collections.emptyList()) : list("NOT IN", values);
    }

    public Q isNull() {
        return query.append(column + " IS NULL", Collections.emptyList());
    }

    public Q isNotNull() {
        return query.append(column + " IS NOT NULL", Collections.emptyList());
    }

    protected Q compare(String operator, Object value) {
        return query.append(column + " " + operator + " ?", Collections.singletonList(toJdbc(value)));
    }

    protected static <V> V requireValue(V value, String operator) {
        return Objects.requireNonNull(value, operator + "() needs a value; use isNull()/isNotNull() for NULL");
    }

    private Q list(String operator, Collection<? extends T> values) {
        StringJoiner placeholders = new StringJoiner(", ", column + " " + operator + " (", ")");
        List<Object> converted = new ArrayList<>(values.size());
        for (T value : values) {
            placeholders.add("?");
            converted.add(toJdbc(value));
        }
        return query.append(placeholders.toString(), converted);
    }

    /**
     * Same conversions as the generated setters, so values reach the driver as JDBC types it accepts.
     */
    static Object toJdbc(Object value) {
        if (value instanceof Instant) {
            return Timestamp.from((Instant) value);
        }
        if (value instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) value);
        }
        if (value instanceof LocalDate) {
            return Date.valueOf((LocalDate) value);
        }
        if (value instanceof LocalTime) {
            return Time.valueOf((LocalTime) value);
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        // java.sql.Date/Time/Timestamp extend java.util.Date and are passed as they are
        if (value != null && value.getClass() == java.util.Date.class) {
            return new Timestamp(((java.util.Date) value).getTime());
        }
        return value;
    }
}
