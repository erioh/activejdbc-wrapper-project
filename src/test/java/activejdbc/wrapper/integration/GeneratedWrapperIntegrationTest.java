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
        AllTypesWrapper written = AllTypesWrapper.builder()
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
        assertThat(read.getLocalDateValue()).isNull();
        assertThat(read.getInstantValue()).isNull();
        assertThat(read.getBigIntegerValue()).isNull();
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

    private AllTypesWrapper saveAndReload(AllTypesWrapper wrapper) {
        AllTypes saved = manipulator.getActivejdbcObject(wrapper);
        assertThat(saved.saveIt()).isTrue();
        AllTypes reloaded = new AllTypes();
        reloaded.setId(saved.getId());
        reloaded.refresh();
        return new AllTypesWrapper(reloaded);
    }
}
