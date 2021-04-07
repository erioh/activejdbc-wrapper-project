package com.sdemenkov;

import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("SOME_TABLE")
@IdName("SOME_ID")
@DbName("SOME_SCHEMA")
@ActiveJdbcRequiredProperty(field = "SOME_ID", clazz = Long.class)
public class AnotherTestClass extends Model {
}
