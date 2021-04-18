package com.sdemenkov;

import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table("FANCY_TABLE")
@IdName("FANCY_TABLE_ID")
@ActiveJdbcRequiredProperty(field = "FANCY_TABLE_ID", clazz = Long.class)
@ActiveJdbcRequiredProperty(field = "BIG_DECIMAL_VALUE", clazz = BigDecimal.class)
@ActiveJdbcRequiredProperty(field = "BOOLEAN_VALUE", clazz = Boolean.class)
@ActiveJdbcRequiredProperty(field = "SQL_DATE", clazz = Date.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_DATE_TIME", clazz = LocalDateTime.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_DATE", clazz = LocalDate.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_TIME", clazz = LocalTime.class)
@ActiveJdbcRequiredProperty(field = "DOUBLE_VALUE", clazz = Double.class)
@ActiveJdbcRequiredProperty(field = "FLOAT_VALUE", clazz = Float.class)
@ActiveJdbcRequiredProperty(field = "INTEGER_VALUE", clazz = Integer.class)
@ActiveJdbcRequiredProperty(field = "LONG_VALUE", clazz = Long.class)
@ActiveJdbcRequiredProperty(field = "SHORT_VALUE", clazz = Short.class)
@ActiveJdbcRequiredProperty(field = "STRING_VALUE", clazz = String.class)
@ActiveJdbcRequiredProperty(field = "TIME_VALUE", clazz = Time.class)
@ActiveJdbcRequiredProperty(field = "TIMESTAMP_VALUE", clazz = Timestamp.class)
@ActiveJdbcRequiredProperty(field = "CLOB_VALUE_FOR_STRING", clazz = String.class)
public class FancyTable extends Model {
}
