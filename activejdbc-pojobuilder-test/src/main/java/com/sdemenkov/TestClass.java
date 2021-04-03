package com.sdemenkov;

import activejdbc.pojo.builder.annotation.ActiveJdbcRequiredProperty;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import java.time.LocalDate;

@Table("SOME_TABLE")
@IdName("SOME_ID")
@DbName("SOME_SCHEMA")
@ActiveJdbcRequiredProperty(field = "CARD_ID", clazz = Long.class)
@ActiveJdbcRequiredProperty(field = "AAA", clazz = String.class)
@ActiveJdbcRequiredProperty(field = "BBB", clazz = LocalDate.class)
public class TestClass extends Model {
}
