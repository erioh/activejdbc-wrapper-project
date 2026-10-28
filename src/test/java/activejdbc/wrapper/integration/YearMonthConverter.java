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

import activejdbc.wrapper.ColumnConverter;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Stores a month as its first day in a DATE column.
 */
public class YearMonthConverter implements ColumnConverter<YearMonth, LocalDate> {
    @Override
    public LocalDate toDatabase(YearMonth value) {
        return value.atDay(1);
    }

    @Override
    public YearMonth fromDatabase(LocalDate value) {
        return YearMonth.from(value);
    }
}
