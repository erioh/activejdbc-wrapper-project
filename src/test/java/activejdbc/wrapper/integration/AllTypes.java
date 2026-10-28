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

import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

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
import java.util.List;
import java.util.UUID;

/**
 * One column per type with a dedicated strategy, plus UUID and Clob for the default (cast) strategy.
 * Registered in activejdbc_models.properties, so it works without ActiveJDBC instrumentation.
 */
@Table("ALL_TYPES")
@ActiveJdbcRequiredProperty(columnName = "ID", clazz = Long.class, readOnly = true, excludeFromEqualsAndHashCode = true)
@ActiveJdbcRequiredProperty(columnName = "STRING_VALUE", clazz = String.class)
@ActiveJdbcRequiredProperty(columnName = "BOOLEAN_VALUE", clazz = Boolean.class)
@ActiveJdbcRequiredProperty(columnName = "SHORT_VALUE", clazz = Short.class)
@ActiveJdbcRequiredProperty(columnName = "INTEGER_VALUE", clazz = Integer.class)
@ActiveJdbcRequiredProperty(columnName = "LONG_VALUE", clazz = Long.class)
@ActiveJdbcRequiredProperty(columnName = "FLOAT_VALUE", clazz = Float.class)
@ActiveJdbcRequiredProperty(columnName = "DOUBLE_VALUE", clazz = Double.class)
@ActiveJdbcRequiredProperty(columnName = "BIG_DECIMAL_VALUE", clazz = BigDecimal.class)
@ActiveJdbcRequiredProperty(columnName = "BIG_INTEGER_VALUE", clazz = BigInteger.class)
@ActiveJdbcRequiredProperty(columnName = "BYTES_VALUE", clazz = byte[].class)
@ActiveJdbcRequiredProperty(columnName = "SQL_DATE_VALUE", clazz = Date.class)
@ActiveJdbcRequiredProperty(columnName = "TIME_VALUE", clazz = Time.class)
@ActiveJdbcRequiredProperty(columnName = "TIMESTAMP_VALUE", clazz = Timestamp.class)
@ActiveJdbcRequiredProperty(columnName = "UTIL_DATE_VALUE", clazz = java.util.Date.class)
@ActiveJdbcRequiredProperty(columnName = "LOCAL_DATE_VALUE", clazz = LocalDate.class)
@ActiveJdbcRequiredProperty(columnName = "LOCAL_TIME_VALUE", clazz = LocalTime.class)
@ActiveJdbcRequiredProperty(columnName = "LOCAL_DATE_TIME_VALUE", clazz = LocalDateTime.class)
@ActiveJdbcRequiredProperty(columnName = "INSTANT_VALUE", clazz = Instant.class)
@ActiveJdbcRequiredProperty(columnName = "UUID_VALUE", clazz = UUID.class)
@ActiveJdbcRequiredProperty(columnName = "CLOB_VALUE", clazz = Clob.class)
@ActiveJdbcRequiredProperty(columnName = "ENUM_VALUE", clazz = Color.class)
@ActiveJdbcRequiredProperty(columnName = "MONEY_VALUE", clazz = Money.class, converter = MoneyConverter.class)
@ActiveJdbcRequiredProperty(columnName = "YEAR_MONTH_VALUE", clazz = YearMonth.class, converter = YearMonthConverter.class)
@ActiveJdbcRequiredProperty(columnName = "TAGS_VALUE", clazz = List.class, converter = TagsConverter.class)
public class AllTypes extends Model {
}
