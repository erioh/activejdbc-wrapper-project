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

package activejdbc.wrapper.integration;

import activejdbc.wrapper.ActivejdbcWrapperManipulator;
import org.javalite.activejdbc.Base;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.UUID;

import javax.sql.rowset.serial.SerialClob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs the generated AllTypesWrapper against a real database: values written through the wrapper must come back
 * through the wrapper with the same type and value. Catches getters/setters that compile but fail at runtime.
 */
public class GeneratedWrapperIntegrationTest {
    private final ActivejdbcWrapperManipulator manipulator = new ActivejdbcWrapperManipulator();

    @BeforeClass
    public static void openDatabase() {
        Base.open("org.h2.Driver", "jdbc:h2:mem:wrapper;DB_CLOSE_DELAY=-1", "sa", "");
        Base.exec("CREATE TABLE ALL_TYPES (" +
                "ID BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "STRING_VALUE VARCHAR(100), " +
                "BOOLEAN_VALUE BOOLEAN, " +
                "SHORT_VALUE SMALLINT, " +
                "INTEGER_VALUE INTEGER, " +
                "LONG_VALUE BIGINT, " +
                "FLOAT_VALUE REAL, " +
                "DOUBLE_VALUE DOUBLE PRECISION, " +
                "BIG_DECIMAL_VALUE DECIMAL(10, 2), " +
                "BIG_INTEGER_VALUE DECIMAL(30, 0), " +
                "BYTES_VALUE VARBINARY(100), " +
                "SQL_DATE_VALUE DATE, " +
                "TIME_VALUE TIME, " +
                "TIMESTAMP_VALUE TIMESTAMP(3), " +
                "UTIL_DATE_VALUE TIMESTAMP(3), " +
                "LOCAL_DATE_VALUE DATE, " +
                "LOCAL_TIME_VALUE TIME, " +
                "LOCAL_DATE_TIME_VALUE TIMESTAMP(3), " +
                "INSTANT_VALUE TIMESTAMP(3), " +
                "UUID_VALUE UUID, " +
                "CLOB_VALUE CLOB, " +
                "ENUM_VALUE VARCHAR(10), " +
                "MONEY_VALUE DECIMAL(10, 2), " +
                "YEAR_MONTH_VALUE DATE, " +
                "TAGS_VALUE VARCHAR(100))");
    }

    @AfterClass
    public static void closeDatabase() {
        Base.exec("DROP TABLE ALL_TYPES");
        Base.close();
    }

    @Before
    public void cleanTable() {
        Base.exec("DELETE FROM ALL_TYPES");
    }

    @Test
    public void should_read_back_every_type_written_through_the_wrapper() {
        // given
        AllTypesWrapper written = fullyPopulated();

        // when
        AllTypesWrapper read = saveAndReload(written);

        // then
        assertThat(read.getStringValue()).isEqualTo("text");
        assertThat(read.getBooleanValue()).isTrue();
        assertThat(read.getShortValue()).isEqualTo((short) 7);
        assertThat(read.getIntegerValue()).isEqualTo(42);
        assertThat(read.getLongValue()).isEqualTo(1234567890123L);
        assertThat(read.getFloatValue()).isEqualTo(1.5f);
        assertThat(read.getDoubleValue()).isEqualTo(2.25);
        assertThat(read.getBigDecimalValue()).isEqualByComparingTo("12345.67");
        assertThat(read.getBigIntegerValue()).isEqualTo(new BigInteger("123456789012345678901234567890"));
        assertThat(read.getBytesValue()).containsExactly(1, 2, 3);
        assertThat(read.getSqlDateValue()).isEqualTo(Date.valueOf("2026-10-27"));
        assertThat(read.getTimeValue()).isEqualTo(Time.valueOf("13:45:30"));
        assertThat(read.getTimestampValue()).isEqualTo(Timestamp.valueOf("2026-10-27 13:45:30.123"));
        // the getter returns a java.sql.Timestamp, which never equals a plain java.util.Date
        assertThat(read.getUtilDateValue().getTime()).isEqualTo(1793000000123L);
        assertThat(read.getLocalDateValue()).isEqualTo(LocalDate.of(2026, 10, 27));
        assertThat(read.getLocalTimeValue()).isEqualTo(LocalTime.of(13, 45, 30));
        assertThat(read.getLocalDateTimeValue()).isEqualTo(LocalDateTime.of(2026, 10, 27, 13, 45, 30, 123_000_000));
        assertThat(read.getInstantValue()).isEqualTo(Instant.parse("2026-10-27T10:15:30.123Z"));
        assertThat(read.getUuidValue()).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        assertThat(read.getEnumValue()).isEqualTo(Color.GREEN);
    }

    @Test
    public void should_read_back_nulls() {
        // given
        AllTypesWrapper written = new AllTypesWrapper();
        written.setStringValue(null);

        // when
        AllTypesWrapper read = saveAndReload(written);

        // then
        assertThat(read).isEqualTo(new AllTypesWrapper());
        assertThat(read.getBooleanValue()).isNull();
        assertThat(read.getLocalDateValue()).isNull();
        assertThat(read.getInstantValue()).isNull();
        assertThat(read.getBigIntegerValue()).isNull();
    }

    @Test
    public void should_tell_null_boolean_from_false() {
        // given
        AllTypesWrapper unset = new AllTypesWrapper();
        AllTypesWrapper no = new AllTypesWrapper();
        no.setBooleanValue(false);

        // when
        AllTypesWrapper readNo = saveAndReload(no);

        // then
        assertThat(unset.getBooleanValue()).isNull();
        assertThat(readNo.getBooleanValue()).isFalse();
        assertThat(unset).isNotEqualTo(no);
    }

    @Test
    public void should_compare_big_decimals_by_value() {
        // given
        AllTypesWrapper oneAndAHalf = new AllTypesWrapper();
        oneAndAHalf.setBigDecimalValue(new BigDecimal("1.5"));
        AllTypesWrapper sameWithTrailingZero = new AllTypesWrapper();
        sameWithTrailingZero.setBigDecimalValue(new BigDecimal("1.50"));
        AllTypesWrapper other = new AllTypesWrapper();
        other.setBigDecimalValue(new BigDecimal("1.51"));

        // when | then
        assertThat(oneAndAHalf).isEqualTo(sameWithTrailingZero).hasSameHashCodeAs(sameWithTrailingZero);
        assertThat(oneAndAHalf).isNotEqualTo(other).isNotEqualTo(new AllTypesWrapper());
        assertThat(new AllTypesWrapper()).isNotEqualTo(oneAndAHalf);
    }

    @Test
    public void should_store_enums_by_name() {
        // given
        AllTypesWrapper written = new AllTypesWrapper();
        written.setEnumValue(Color.RED);

        // when
        AllTypesWrapper read = saveAndReload(written);

        // then
        assertThat(Base.firstCell("SELECT ENUM_VALUE FROM ALL_TYPES")).isEqualTo("RED");
        assertThat(read.getEnumValue()).isEqualTo(Color.RED);
    }

    @Test
    public void should_read_dates_and_times_from_values_only_get_timestamp_parses() {
        // given: like oracle.sql.TIMESTAMP, which Oracle returns for TIMESTAMP columns
        Object driverTimestamp = new Object() {
            @Override
            public String toString() {
                return "2026-10-27 13:45:30.5";
            }
        };
        AllTypes model = new AllTypes();
        model.set("SQL_DATE_VALUE", driverTimestamp);
        model.set("TIME_VALUE", driverTimestamp);
        model.set("LOCAL_DATE_VALUE", driverTimestamp);
        model.set("LOCAL_TIME_VALUE", driverTimestamp);
        long millis = Timestamp.valueOf("2026-10-27 13:45:30.5").getTime();

        // when
        AllTypesWrapper wrapper = new AllTypesWrapper(model);

        // then
        assertThat(wrapper.getSqlDateValue()).isEqualTo(new Date(millis));
        assertThat(wrapper.getTimeValue()).isEqualTo(new Time(millis));
        assertThat(wrapper.getLocalDateValue()).isEqualTo(LocalDate.of(2026, 10, 27));
        assertThat(wrapper.getLocalTimeValue()).isEqualTo(LocalTime.of(13, 45, 30));
    }

    @Test
    public void should_still_read_dates_and_times_in_their_own_format() {
        // given
        AllTypes model = new AllTypes();
        model.set("LOCAL_DATE_VALUE", "2026-10-27");
        model.set("LOCAL_TIME_VALUE", "13:45:30");
        model.set("SQL_DATE_VALUE", "not a date");

        // when
        AllTypesWrapper wrapper = new AllTypesWrapper(model);

        // then
        assertThat(wrapper.getLocalDateValue()).isEqualTo(LocalDate.of(2026, 10, 27));
        assertThat(wrapper.getLocalTimeValue()).isEqualTo(LocalTime.of(13, 45, 30));
        assertThatThrownBy(wrapper::getSqlDateValue).hasMessageContaining("not a date");
    }

    @Test
    public void should_consider_wrappers_with_same_values_equal() {
        // given
        AllTypesWrapper first = AllTypesWrapper.builder().stringValue("a").bytesValue(new byte[]{1, 2}).build();
        AllTypesWrapper second = AllTypesWrapper.builder().stringValue("a").bytesValue(new byte[]{1, 2}).build();

        // when | then
        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    public void should_read_back_clob_while_connection_is_open() throws Exception {
        // given
        AllTypesWrapper written = new AllTypesWrapper();
        written.setClobValue(new SerialClob("hello clob".toCharArray()));

        // when
        AllTypesWrapper read = saveAndReload(written);

        // then: the driver's own Clob, readable as long as the connection is open
        Clob clob = read.getClobValue();
        assertThat(clob.getSubString(1, (int) clob.length())).isEqualTo("hello clob");
    }

    @Test
    public void should_find_rows_with_query_builder() {
        // given
        save(AllTypesWrapper.builder().stringValue("alpha").integerValue(1).build());
        save(AllTypesWrapper.builder().stringValue("beta").integerValue(2).build());
        save(AllTypesWrapper.builder().stringValue("gamma").integerValue(3).build());
        save(AllTypesWrapper.builder().stringValue("delta").integerValue(4).build());

        // when
        AllTypesWrapper.AllTypesWrapperQuery query = AllTypesWrapper.query()
                .integerValue().gt(1)
                .and(q -> q.stringValue().eq("beta").or().stringValue().like("g%"));

        // then
        assertThat(query.getSql()).isEqualTo("INTEGER_VALUE > ? AND (STRING_VALUE = ? OR STRING_VALUE LIKE ?)");
        assertThat(query.fetch()).extracting(AllTypesWrapper::getStringValue).containsExactlyInAnyOrder("beta", "gamma");
        assertThat(query.fetchModels()).hasSize(2);
        assertThat(query.fetchCount()).isEqualTo(2L);
    }

    @Test
    public void should_combine_or_and_null_checks() {
        // given
        save(AllTypesWrapper.builder().stringValue("alpha").integerValue(1).build());
        save(AllTypesWrapper.builder().stringValue("beta").build());
        save(AllTypesWrapper.builder().stringValue("gamma").integerValue(3).build());

        // when | then
        assertThat(AllTypesWrapper.query().integerValue().isNull().or().integerValue().in(3, 4).fetch())
                .extracting(AllTypesWrapper::getStringValue).containsExactlyInAnyOrder("beta", "gamma");
        assertThat(AllTypesWrapper.query().integerValue().eq(null).fetchCount()).isEqualTo(1L);
        assertThat(AllTypesWrapper.query().integerValue().in(java.util.Collections.emptyList()).fetchCount()).isZero();
        assertThat(AllTypesWrapper.query().fetchCount()).isEqualTo(3L);
        assertThat(AllTypesWrapper.query().fetch()).hasSize(3);
    }

    @Test
    public void should_sort_and_page_results() {
        // given
        save(AllTypesWrapper.builder().stringValue("alpha").integerValue(1).longValue(20L).build());
        save(AllTypesWrapper.builder().stringValue("beta").integerValue(2).longValue(10L).build());
        save(AllTypesWrapper.builder().stringValue("gamma").integerValue(3).longValue(10L).build());
        save(AllTypesWrapper.builder().stringValue("delta").integerValue(4).longValue(10L).build());

        // when | then
        assertThat(AllTypesWrapper.query().integerValue().desc().limit(2).fetch())
                .extracting(AllTypesWrapper::getStringValue).containsExactly("delta", "gamma");
        assertThat(AllTypesWrapper.query().integerValue().asc().limit(2).offset(1).fetch())
                .extracting(AllTypesWrapper::getStringValue).containsExactly("beta", "gamma");
        // several sort columns apply in the order they were added
        assertThat(AllTypesWrapper.query().longValue().asc().stringValue().desc().fetch())
                .extracting(AllTypesWrapper::getStringValue).containsExactly("gamma", "delta", "beta", "alpha");
        // conditions and sorting together; fetchCount ignores limit
        AllTypesWrapper.AllTypesWrapperQuery query = AllTypesWrapper.query().stringValue().like("%a").integerValue().desc().limit(1);
        assertThat(query.fetch()).extracting(AllTypesWrapper::getStringValue).containsExactly("delta");
        assertThat(query.fetchModels()).hasSize(1);
        assertThat(query.fetchCount()).isEqualTo(4L);
    }

    @Test
    public void should_compare_wrappers_without_read_only_id() {
        // given
        AllTypesWrapper saved = saveAndReload(fullyPopulated());

        // when | then: the ID comes from the database and is not part of equals/hashCode
        assertThat(saved.getId()).isNotNull();
        assertThat(saved).isEqualTo(fullyPopulated()).hasSameHashCodeAs(fullyPopulated());
        assertThat(AllTypesWrapper.query().id().eq(saved.getId()).fetch()).containsExactly(fullyPopulated());
    }

    @Test
    public void should_copy_wrapper_with_to_builder() {
        // given
        AllTypesWrapper saved = saveAndReload(fullyPopulated());

        // when
        AllTypesWrapper copy = saved.toBuilder().stringValue("copy").build();

        // then: every writable property is copied, the read-only ID is not, the original is unchanged
        assertThat(copy.getId()).isNull();
        assertThat(copy).isEqualTo(fullyPopulated().toBuilder().stringValue("copy").build());
        assertThat(saved.getStringValue()).isEqualTo("text");
        // so the copy is saved as a new row
        save(copy);
        assertThat(copy.getId()).isNotNull().isNotEqualTo(saved.getId());
        assertThat(AllTypesWrapper.query().fetchCount()).isEqualTo(2L);
    }

    @Test
    public void should_fetch_first_row_only() {
        // given
        save(AllTypesWrapper.builder().stringValue("alpha").integerValue(1).build());
        save(AllTypesWrapper.builder().stringValue("beta").integerValue(2).build());
        save(AllTypesWrapper.builder().stringValue("gamma").integerValue(3).build());
        AllTypesWrapper.AllTypesWrapperQuery query = AllTypesWrapper.query().integerValue().gt(1).integerValue().desc();

        // when | then
        assertThat(query.fetchFirst()).map(AllTypesWrapper::getStringValue).contains("gamma");
        assertThat(AllTypesWrapper.query().integerValue().asc().offset(1).fetchFirst())
                .map(AllTypesWrapper::getStringValue).contains("beta");
        assertThat(AllTypesWrapper.query().stringValue().eq("omega").fetchFirst()).isEmpty();
        assertThat(AllTypesWrapper.query().limit(0).fetchFirst()).isEmpty();
        // the query itself is unchanged
        assertThat(query.fetch()).extracting(AllTypesWrapper::getStringValue).containsExactly("gamma", "beta");
    }

    @Test
    public void should_fetch_the_only_row() {
        // given
        save(AllTypesWrapper.builder().stringValue("alpha").integerValue(1).build());
        save(AllTypesWrapper.builder().stringValue("beta").integerValue(2).build());
        save(AllTypesWrapper.builder().stringValue("gamma").integerValue(2).build());

        // when | then
        assertThat(AllTypesWrapper.query().integerValue().eq(1).fetchOne()).map(AllTypesWrapper::getStringValue).contains("alpha");
        assertThat(AllTypesWrapper.query().stringValue().eq("omega").fetchOne()).isEmpty();
        assertThatThrownBy(() -> AllTypesWrapper.query().integerValue().eq(2).fetchOne())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("More than one row matches the query: INTEGER_VALUE = ? [2]");
        // limit and offset apply as in fetch()
        assertThat(AllTypesWrapper.query().integerValue().eq(2).stringValue().asc().limit(1).fetchOne())
                .map(AllTypesWrapper::getStringValue).contains("beta");
        assertThat(AllTypesWrapper.query().integerValue().asc().stringValue().asc().offset(2).fetchOne())
                .map(AllTypesWrapper::getStringValue).contains("gamma");
        assertThat(AllTypesWrapper.query().integerValue().eq(1).limit(0).fetchOne()).isEmpty();
    }

    @Test
    public void should_check_whether_rows_exist() {
        // given
        save(AllTypesWrapper.builder().stringValue("alpha").integerValue(1).build());
        save(AllTypesWrapper.builder().stringValue("beta").integerValue(2).build());

        // when | then
        assertThat(AllTypesWrapper.query().exists()).isTrue();
        assertThat(AllTypesWrapper.query().integerValue().gt(1).exists()).isTrue();
        assertThat(AllTypesWrapper.query().stringValue().eq("omega").exists()).isFalse();
        // limit and offset apply as in fetch()
        assertThat(AllTypesWrapper.query().offset(1).exists()).isTrue();
        assertThat(AllTypesWrapper.query().offset(2).exists()).isFalse();
        assertThat(AllTypesWrapper.query().limit(0).exists()).isFalse();
    }

    @Test
    public void should_find_row_by_every_type() {
        // given: the fully populated row and one with different values
        save(fullyPopulated());
        save(AllTypesWrapper.builder().stringValue("other").booleanValue(false).build());
        AllTypesWrapper expected = fullyPopulated();

        // when | then: each query must match exactly the populated row, which also checks the JDBC conversions
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().stringValue().eq(expected.getStringValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().booleanValue().eq(expected.getBooleanValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().shortValue().eq(expected.getShortValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().integerValue().eq(expected.getIntegerValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().longValue().eq(expected.getLongValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().floatValue().eq(expected.getFloatValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().doubleValue().eq(expected.getDoubleValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().bigDecimalValue().eq(expected.getBigDecimalValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().bigIntegerValue().eq(expected.getBigIntegerValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().bytesValue().eq(expected.getBytesValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().sqlDateValue().eq(expected.getSqlDateValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().timeValue().eq(expected.getTimeValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().timestampValue().eq(expected.getTimestampValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().utilDateValue().eq(expected.getUtilDateValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().localDateValue().eq(expected.getLocalDateValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().localTimeValue().eq(expected.getLocalTimeValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().localDateTimeValue().eq(expected.getLocalDateTimeValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().instantValue().eq(expected.getInstantValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().uuidValue().eq(expected.getUuidValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().enumValue().eq(expected.getEnumValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().enumValue().in(Color.GREEN, Color.RED));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().localDateValue().between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().moneyValue().eq(expected.getMoneyValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().yearMonthValue().eq(expected.getYearMonthValue()));
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().tagsValue().eq(expected.getTagsValue()));
    }

    @Test
    public void should_convert_properties_with_column_converter() {
        // given
        AllTypesWrapper saved = saveAndReload(fullyPopulated());

        // when | then: stored as the converter's database type
        assertThat(saved.getMoneyValue()).isEqualTo(Money.of("19.99"));
        assertThat(saved.getYearMonthValue()).isEqualTo(YearMonth.of(2026, 10));
        assertThat(saved.getTagsValue()).containsExactly("red", "blue");
        assertThat(Base.firstCell("SELECT MONEY_VALUE FROM ALL_TYPES")).isEqualTo(new BigDecimal("19.99"));
        assertThat(Base.firstCell("SELECT TAGS_VALUE FROM ALL_TYPES")).isEqualTo("red,blue");
        assertThat(Base.firstCell("SELECT YEAR_MONTH_VALUE FROM ALL_TYPES").toString()).isEqualTo("2026-10-01");
        // NULL never reaches the converter
        AllTypesWrapper empty = saveAndReload(AllTypesWrapper.builder().stringValue("empty").build());
        assertThat(empty.getMoneyValue()).isNull();
        assertThat(empty.getTagsValue()).isNull();
        empty.setYearMonthValue(null);
        assertThat(empty.getYearMonthValue()).isNull();
        // query parameters are converted too, including ranges and lists
        assertThat(AllTypesWrapper.query().moneyValue().gt(Money.of("10")).fetch()).containsExactly(saved);
        assertThat(AllTypesWrapper.query().yearMonthValue().between(YearMonth.of(2026, 1), YearMonth.of(2026, 12)).fetch()).containsExactly(saved);
        assertThat(AllTypesWrapper.query().moneyValue().in(Money.of("19.99"), Money.of("5")).fetch()).containsExactly(saved);
        assertThat(AllTypesWrapper.query().moneyValue().isNull().fetch()).extracting(AllTypesWrapper::getStringValue).containsExactly("empty");
    }

    private static void assertMatchesOnlyPopulatedRow(AllTypesWrapper.AllTypesWrapperQuery query) {
        assertThat(query.fetch()).as(query.getSql()).extracting(AllTypesWrapper::getStringValue).containsExactly("text");
    }

    private static AllTypesWrapper fullyPopulated() {
        return AllTypesWrapper.builder()
                .stringValue("text")
                .booleanValue(true)
                .shortValue((short) 7)
                .integerValue(42)
                .longValue(1234567890123L)
                .floatValue(1.5f)
                .doubleValue(2.25)
                .bigDecimalValue(new BigDecimal("12345.67"))
                .bigIntegerValue(new BigInteger("123456789012345678901234567890"))
                .bytesValue(new byte[]{1, 2, 3})
                .sqlDateValue(Date.valueOf("2026-10-27"))
                .timeValue(Time.valueOf("13:45:30"))
                .timestampValue(Timestamp.valueOf("2026-10-27 13:45:30.123"))
                .utilDateValue(new java.util.Date(1793000000123L))
                .localDateValue(LocalDate.of(2026, 10, 27))
                .localTimeValue(LocalTime.of(13, 45, 30))
                .localDateTimeValue(LocalDateTime.of(2026, 10, 27, 13, 45, 30, 123_000_000))
                .instantValue(Instant.parse("2026-10-27T10:15:30.123Z"))
                .uuidValue(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .enumValue(Color.GREEN)
                .moneyValue(Money.of("19.99"))
                .yearMonthValue(YearMonth.of(2026, 10))
                .tagsValue(Arrays.asList("red", "blue"))
                .build();
    }

    private void save(AllTypesWrapper wrapper) {
        assertThat(manipulator.getActivejdbcObject(wrapper).saveIt()).isTrue();
    }

    private AllTypesWrapper saveAndReload(AllTypesWrapper wrapper) {
        AllTypes saved = manipulator.getActivejdbcObject(wrapper);
        assertThat(saved.saveIt()).isTrue();
        AllTypes reloaded = new AllTypes();
        reloaded.setId(saved.getId());
        reloaded.refresh();
        return new AllTypesWrapper(reloaded);
    }
}
