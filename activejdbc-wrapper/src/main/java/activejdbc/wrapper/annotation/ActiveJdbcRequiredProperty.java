package activejdbc.wrapper.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(value = ActiveJdbcRequiredProperties.class)
public @interface ActiveJdbcRequiredProperty {
    String field();

    Class<?> clazz();
}
