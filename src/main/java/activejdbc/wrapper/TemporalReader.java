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

/**
 * Reads date and time properties for the generated getters. ActiveJDBC's getDate/getTime only convert java.util.Date
 * values and strings in their own format; some drivers return other objects, e.g. Oracle's oracle.sql.TIMESTAMP for
 * TIMESTAMP columns, which only getTimestamp can parse. Such values are read through getTimestamp instead.
 */
public final class TemporalReader {

    private TemporalReader() {
    }

    public static Date sqlDate(Model model, String column) {
        try {
            return model.getDate(column);
        } catch (RuntimeException notADate) {
            Timestamp timestamp = timestamp(model, column, notADate);
            return timestamp == null ? null : new Date(timestamp.getTime());
        }
    }

    public static LocalDate localDate(Model model, String column) {
        Date date = sqlDate(model, column);
        return date == null ? null : date.toLocalDate();
    }

    public static Time time(Model model, String column) {
        try {
            return model.getTime(column);
        } catch (RuntimeException notATime) {
            Timestamp timestamp = timestamp(model, column, notATime);
            return timestamp == null ? null : new Time(timestamp.getTime());
        }
    }

    public static LocalTime localTime(Model model, String column) {
        Time time = time(model, column);
        return time == null ? null : time.toLocalTime();
    }

    private static Timestamp timestamp(Model model, String column, RuntimeException original) {
        try {
            return model.getTimestamp(column);
        } catch (RuntimeException alsoNotATimestamp) {
            original.addSuppressed(alsoNotATimestamp);
            throw original;
        }
    }
}
