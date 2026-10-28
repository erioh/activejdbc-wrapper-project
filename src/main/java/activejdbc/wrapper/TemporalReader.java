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

import org.javalite.activejdbc.Model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * Reads date and time properties for the generated getters. ActiveJDBC's getDate/getTime convert java.util.Date
 * values, numbers and strings in the date or time format. Some drivers return their own objects instead, e.g.
 * Oracle's oracle.sql.TIMESTAMP for TIMESTAMP columns, whose toString() is in the timestamp format: getDate/getTime
 * fail on them, getTimestamp parses them. Such values are read through getTimestamp, everything else exactly as
 * before.
 */
public final class TemporalReader {

    /** What {@link Timestamp#valueOf(String)} accepts. */
    private static final Pattern TIMESTAMP_FORMAT =
            Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}(\\.\\d{1,9})?");

    private TemporalReader() {
    }

    public static Date sqlDate(Model model, String column) {
        if (needsGetTimestamp(model.get(column))) {
            Timestamp timestamp = model.getTimestamp(column);
            return timestamp == null ? null : new Date(timestamp.getTime());
        }
        return model.getDate(column);
    }

    public static LocalDate localDate(Model model, String column) {
        Date date = sqlDate(model, column);
        return date == null ? null : date.toLocalDate();
    }

    public static Time time(Model model, String column) {
        if (needsGetTimestamp(model.get(column))) {
            Timestamp timestamp = model.getTimestamp(column);
            return timestamp == null ? null : new Time(timestamp.getTime());
        }
        return model.getTime(column);
    }

    public static LocalTime localTime(Model model, String column) {
        Time time = time(model, column);
        return time == null ? null : time.toLocalTime();
    }

    /**
     * java.util.Date values (java.sql types included) and numbers are converted by getDate/getTime directly; other
     * values are converted through toString(), which getDate/getTime can't parse when it holds date and time.
     */
    static boolean needsGetTimestamp(Object value) {
        return value != null
                && !(value instanceof java.util.Date)
                && !(value instanceof Number)
                && TIMESTAMP_FORMAT.matcher(value.toString().trim()).matches();
    }
}
