package com.sdemenkov;

import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("FANCY_TABLE")
@IdName("FANCY_TABLE_ID")
@ActiveJdbcRequiredProperty(columnName = "FANCY_TABLE_ID", clazz = Long.class, desiredFieldName = "customColumn")
public class FancyTableWithCustomColumn extends Model {
}
