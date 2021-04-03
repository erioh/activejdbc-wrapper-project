package activejdbc.pojo.builder.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(value = ActiveJdbcRequiredProperties.class)
public @interface ActiveJdbcRequiredProperty {
    String field();

    Class<?> clazz();
}
