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

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.javalite.activejdbc.Model;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(DataProviderRunner.class)
public class AbstractQueryTest {

    @Test
    public void should_build_comparison_operators() {
        assertSql(query().age().eq(1), "AGE = ?", 1);
        assertSql(query().age().ne(1), "AGE <> ?", 1);
        assertSql(query().age().gt(1), "AGE > ?", 1);
        assertSql(query().age().ge(1), "AGE >= ?", 1);
        assertSql(query().age().lt(1), "AGE < ?", 1);
        assertSql(query().age().le(1), "AGE <= ?", 1);
        assertSql(query().age().between(1, 9), "AGE BETWEEN ? AND ?", 1, 9);
        assertSql(query().name().like("Jo%"), "NAME LIKE ?", "Jo%");
        assertSql(query().name().notLike("Jo%"), "NAME NOT LIKE ?", "Jo%");
    }

    @Test
    public void should_build_null_checks() {
        assertSql(query().name().isNull(), "NAME IS NULL");
        assertSql(query().name().isNotNull(), "NAME IS NOT NULL");
        // = NULL never matches in SQL
        assertSql(query().name().eq(null), "NAME IS NULL");
        assertSql(query().name().ne(null), "NAME IS NOT NULL");
    }

    @Test
    public void should_build_in() {
        assertSql(query().age().in(1, 2, 3), "AGE IN (?, ?, ?)", 1, 2, 3);
        assertSql(query().age().notIn(Arrays.asList(1, 2)), "AGE NOT IN (?, ?)", 1, 2);
        // IN () is not valid SQL
        assertSql(query().age().in(Collections.emptyList()), "1 = 0");
        assertSql(query().age().notIn(Collections.emptyList()), "1 = 1");
    }

    @Test
    public void should_join_conditions_with_and_by_default() {
        assertSql(query().name().eq("a").age().gt(1), "NAME = ? AND AGE > ?", "a", 1);
        assertSql(query().name().eq("a").and().age().gt(1), "NAME = ? AND AGE > ?", "a", 1);
        assertSql(query().name().eq("a").or().age().gt(1), "NAME = ? OR AGE > ?", "a", 1);
    }

    @Test
    public void should_put_groups_in_parentheses() {
        assertSql(query().age().eq(1).and(q -> q.name().eq("a").or().name().eq("b")),
                "AGE = ? AND (NAME = ? OR NAME = ?)", 1, "a", "b");
        assertSql(query().age().eq(1).or(q -> q.name().eq("a").and().age().lt(5)),
                "AGE = ? OR (NAME = ? AND AGE < ?)", 1, "a", 5);
        assertSql(query().group(q -> q.name().eq("a").or().name().eq("b")).age().eq(1),
                "(NAME = ? OR NAME = ?) AND AGE = ?", "a", "b", 1);
        assertSql(query().group(q -> q.age().eq(1).or(inner -> inner.name().eq("a").name().eq("b"))),
                "(AGE = ? OR (NAME = ? AND NAME = ?))", 1, "a", "b");
    }

    @DataProvider
    public static Object[][] jdbc_conversions() {
        return new Object[][]{
                {Instant.parse("2026-10-27T10:15:30.123Z"), Timestamp.from(Instant.parse("2026-10-27T10:15:30.123Z"))},
                {LocalDateTime.of(2026, 10, 27, 13, 45), Timestamp.valueOf("2026-10-27 13:45:00")},
                {LocalDate.of(2026, 10, 27), Date.valueOf("2026-10-27")},
                {LocalTime.of(13, 45, 30), Time.valueOf("13:45:30")},
                {new BigInteger("12345678901234567890"), new BigDecimal("12345678901234567890")},
                {new java.util.Date(1793000000123L), new Timestamp(1793000000123L)},
                {Timestamp.valueOf("2026-10-27 13:45:00"), Timestamp.valueOf("2026-10-27 13:45:00")},
                {"text", "text"},
        };
    }

    @Test
    @UseDataProvider("jdbc_conversions")
    public void should_convert_values_for_jdbc(Object value, Object expected) {
        // when
        TestQuery query = query().any().eq(value);
        // then
        assertThat(query.getParams()).containsExactly(expected);
        assertThat(query.getParams().get(0)).isExactlyInstanceOf(expected.getClass());
    }

    @Test
    public void should_convert_values_in_lists_and_ranges() {
        LocalDate first = LocalDate.of(2026, 1, 1);
        LocalDate last = LocalDate.of(2026, 12, 31);
        assertThat(query().any().in(first, last).getParams()).containsExactly(Date.valueOf(first), Date.valueOf(last));
        assertThat(query().any().between(first, last).getParams()).containsExactly(Date.valueOf(first), Date.valueOf(last));
    }

    @DataProvider
    public static Object[][] misuses() {
        return new Object[][]{
                {(Function<TestQuery, Object>) q -> q.and(), "and() needs a condition before it"},
                {(Function<TestQuery, Object>) q -> q.or(), "or() needs a condition before it"},
                {(Function<TestQuery, Object>) q -> q.age().eq(1).and().or(), "or() follows and(), add a condition between them"},
                {(Function<TestQuery, Object>) q -> q.age().eq(1).or().fetchModels(), "the query ends with or(), add a condition after it"},
                {(Function<TestQuery, Object>) q -> q.age().eq(1).and().fetchCount(), "the query ends with and(), add a condition after it"},
                {(Function<TestQuery, Object>) q -> q.group(inner -> inner), "group() needs at least one condition"},
                {(Function<TestQuery, Object>) q -> q.group(inner -> inner.age().eq(1).or()), "the query ends with or(), add a condition after it"},
        };
    }

    @Test
    @UseDataProvider("misuses")
    public void should_reject_misuse(Function<TestQuery, Object> misuse, String message) {
        assertThatThrownBy(() -> misuse.apply(query()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(message);
    }

    @Test
    public void should_reject_null_for_operators_without_null_meaning() {
        assertThatThrownBy(() -> query().age().gt(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("gt() needs a value; use isNull()/isNotNull() for NULL");
    }

    @Test
    public void should_not_expose_params_for_modification() {
        assertThatThrownBy(() -> query().age().eq(1).getParams().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static void assertSql(TestQuery query, String sql, Object... params) {
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getParams()).containsExactly(params);
    }

    private static TestQuery query() {
        return new TestQuery();
    }

    public static class Thing extends Model {
    }

    /**
     * Same shape as a generated query class.
     */
    public static class TestQuery extends AbstractQuery<TestQuery, Thing, Thing> {
        TestQuery() {
            super(Thing.class, Function.identity());
        }

        @Override
        protected TestQuery newQuery() {
            return new TestQuery();
        }

        StringCondition<TestQuery> name() {
            return newStringCondition("NAME");
        }

        Condition<TestQuery, Integer> age() {
            return newCondition("AGE");
        }

        Condition<TestQuery, Object> any() {
            return newCondition("ANY_COLUMN");
        }
    }
}
