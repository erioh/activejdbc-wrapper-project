package gradleit;

import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("ITEM")
@ActiveJdbcRequiredProperty(columnName = "ID", clazz = Long.class)
@ActiveJdbcRequiredProperty(columnName = "NAME", clazz = String.class)
@ActiveJdbcRequiredProperty(columnName = "PRICE", clazz = BigDecimal.class)
@ActiveJdbcRequiredProperty(columnName = "CREATED", clazz = LocalDate.class)
@ActiveJdbcRequiredProperty(columnName = "STATUS", clazz = Status.class)
public class Item extends Model {
}
