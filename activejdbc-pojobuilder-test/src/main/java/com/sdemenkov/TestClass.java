package com.sdemenkov;

import activejdbc.pojo.builder.annotation.ActiveJdbcRequiredProperty;

import java.time.LocalDate;

@ActiveJdbcRequiredProperty(field = "CARD_ID", clazz = Long.class)
@ActiveJdbcRequiredProperty(field = "AAA", clazz = String.class)
@ActiveJdbcRequiredProperty(field = "BBB", clazz = LocalDate.class)
public class TestClass {
}
