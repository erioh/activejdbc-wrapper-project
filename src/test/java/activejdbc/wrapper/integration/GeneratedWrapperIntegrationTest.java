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
import java.util.UUID;

import javax.sql.rowset.serial.SerialClob;

import static org.assertj.core.api.Assertions.assertThat;

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
                "CLOB_VALUE CLOB)");
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
        assertMatchesOnlyPopulatedRow(AllTypesWrapper.query().localDateValue().between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)));
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
