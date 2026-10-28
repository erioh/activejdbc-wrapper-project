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

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.javalite.activejdbc.Model;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class TemporalReaderTest {

    private static final String COLUMN = "CREATED";
    private static final Timestamp TIMESTAMP = Timestamp.valueOf("2026-10-27 13:45:30.5");

    /** Like oracle.sql.TIMESTAMP: not a java.util.Date, toString() in the timestamp format. */
    private static final Object DRIVER_TIMESTAMP = new Object() {
        @Override
        public String toString() {
            return "2026-10-27 13:45:30.5";
        }
    };

    private final Model model = mock(Model.class);

    @DataProvider
    public static Object[][] values_get_date_and_get_time_convert() {
        return new Object[][]{
                {null},
                {Date.valueOf("2026-10-27")},
                {Time.valueOf("13:45:30")},
                {TIMESTAMP},
                {new java.util.Date(1793000000123L)},
                {1793000000123L},
                {"2026-10-27"},
                {"13:45:30"},
                {"not a date"},
        };
    }

    @DataProvider
    public static Object[][] values_only_get_timestamp_converts() {
        return new Object[][]{
                {DRIVER_TIMESTAMP},
                {"2026-10-27 13:45:30.5"},
                {"2026-10-27 13:45:30"},
                {" 2026-10-27 13:45:30.123456789 "},
        };
    }

    @Test
    @UseDataProvider("values_get_date_and_get_time_convert")
    public void should_tell_values_get_date_and_get_time_convert(Object value) {
        // when | then
        assertThat(TemporalReader.needsGetTimestamp(value)).isFalse();
    }

    @Test
    @UseDataProvider("values_only_get_timestamp_converts")
    public void should_tell_values_only_get_timestamp_converts(Object value) {
        // when | then
        assertThat(TemporalReader.needsGetTimestamp(value)).isTrue();
    }

    @Test
    public void should_read_sql_date_with_get_date() {
        // given
        Date date = Date.valueOf("2026-10-27");
        when(model.get(COLUMN)).thenReturn(date);
        when(model.getDate(COLUMN)).thenReturn(date);

        // when | then
        assertThat(TemporalReader.sqlDate(model, COLUMN)).isEqualTo(date);
        assertThat(TemporalReader.localDate(model, COLUMN)).isEqualTo(LocalDate.of(2026, 10, 27));
        verify(model, never()).getTimestamp(anyString());
    }

    @Test
    public void should_read_sql_date_of_driver_timestamp_with_get_timestamp() {
        // given
        when(model.get(COLUMN)).thenReturn(DRIVER_TIMESTAMP);
        when(model.getTimestamp(COLUMN)).thenReturn(TIMESTAMP);

        // when | then
        assertThat(TemporalReader.sqlDate(model, COLUMN)).isEqualTo(new Date(TIMESTAMP.getTime()));
        assertThat(TemporalReader.localDate(model, COLUMN)).isEqualTo(LocalDate.of(2026, 10, 27));
        verify(model, never()).getDate(anyString());
    }

    @Test
    public void should_read_time_with_get_time() {
        // given
        Time time = Time.valueOf("13:45:30");
        when(model.get(COLUMN)).thenReturn(time);
        when(model.getTime(COLUMN)).thenReturn(time);

        // when | then
        assertThat(TemporalReader.time(model, COLUMN)).isEqualTo(time);
        assertThat(TemporalReader.localTime(model, COLUMN)).isEqualTo(LocalTime.of(13, 45, 30));
        verify(model, never()).getTimestamp(anyString());
    }

    @Test
    public void should_read_time_of_driver_timestamp_with_get_timestamp() {
        // given
        when(model.get(COLUMN)).thenReturn(DRIVER_TIMESTAMP);
        when(model.getTimestamp(COLUMN)).thenReturn(TIMESTAMP);

        // when | then
        assertThat(TemporalReader.time(model, COLUMN)).isEqualTo(new Time(TIMESTAMP.getTime()));
        assertThat(TemporalReader.localTime(model, COLUMN)).isEqualTo(LocalTime.of(13, 45, 30));
        verify(model, never()).getTime(anyString());
    }

    @Test
    public void should_return_null_for_null() {
        // when | then
        assertThat(TemporalReader.sqlDate(model, COLUMN)).isNull();
        assertThat(TemporalReader.localDate(model, COLUMN)).isNull();
        assertThat(TemporalReader.time(model, COLUMN)).isNull();
        assertThat(TemporalReader.localTime(model, COLUMN)).isNull();
    }
}
