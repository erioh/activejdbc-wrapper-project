package com.sdemenkov;

import activejdbc.pojo.builder.annotation.ActiveJdbcRequiredProperty;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table("SOME_TABLE")
@IdName("SOME_ID")
@DbName("SOME_SCHEMA")
@ActiveJdbcRequiredProperty(field = "SOME_ID", clazz = Long.class)
@ActiveJdbcRequiredProperty(field = "AAA", clazz = String.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_DATE", clazz = LocalDate.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_DATE_TIME", clazz = LocalDateTime.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_TIME", clazz = LocalTime.class)
public class TestClass extends Model {
}
