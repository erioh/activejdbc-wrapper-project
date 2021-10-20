# activejdbc-wrapper-project

The main goal of this project is to simplify usage of ActiveJDBC by generation new wrapper classes.

Maybe you have noticed that sometimes activejdbc code looks a bit ugly.
At the same time, you need to remember the names of all fields, types of these fields, etc.

If you are using ActiveJDBC in your e2e/integration tests, you'll not be able to do simple assertions. You'll be forced to call a ton of getters in order to fetch needed data.
Also, sometimes you really missing equals and toString methods.
And you don't want to create these methods on your own.

So. Using this project you'll be able insteasd of this

```java
        FancyTable activejdbcObject = new FancyTable();
        activejdbcObject.setString("FIRST_NAME", "Some value");
        activejdbcObject.setBigDecimal("AMOUNT", BigDecimal.TEN);
        // 100500 billions of another setters
        activejdbcObject.setBoolean("ACTIVATED", false);
```

do something like that.
```java
        FancyTableWrapper fancyTableWrapper = new FancyTableWrapper();
        fancyTableWrapper.setFirstName("Some value");
        fancyTableWrapper.setAmount(BigDecimal.TEN);
        fancyTableWrapper.setActivated(false);
```
In case if you prefer to use builders, you can use them as well :)
```java
        SomeTableWrapper someTableWrapper = SomeTableWrapper.builder()
                .withSomeId(1L)
                .withAaa("some value")
                .build();
```
All setters and getters should be generated automatically.
Methods equals/hashCode/toString would be overridden automatically as well, so they are ready to use.

# So, what do we need to start?
0. Let's assume that you already have some ActiveJDBC classes, everything works and you want only to simplify usage of these classes.
```java
@Table("FANCY_TABLE")
@IdName("FANCY_TABLE_ID")
public class FancyTable extends Model {
}
```
1. You need to add new dependency into your pom.xml file.
```xml
        <dependency>
            <groupId>io.github.erioh</groupId>
            <artifactId>activejdbc-wrapper</artifactId>
            <version>1.0.2</version>
        </dependency>
```
2. Put ActiveJdbcRequiredProperty annotation with provided column name and expected type
```java
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
@ActiveJdbcRequiredProperty(field = "AMOUNT", clazz = BigDecimal.class)
@ActiveJdbcRequiredProperty(field = "ACTIVATED", clazz = Boolean.class)
@ActiveJdbcRequiredProperty(field = "SQL_DATE", clazz = Date.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_DATE_TIME", clazz = LocalDateTime.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_DATE", clazz = LocalDate.class)
@ActiveJdbcRequiredProperty(field = "LOCAL_TIME", clazz = LocalTime.class)
@ActiveJdbcRequiredProperty(field = "DOUBLE_VALUE", clazz = Double.class)
@ActiveJdbcRequiredProperty(field = "FLOAT_VALUE", clazz = Float.class)
@ActiveJdbcRequiredProperty(field = "INTEGER_VALUE", clazz = Integer.class)
@ActiveJdbcRequiredProperty(field = "LONG_VALUE", clazz = Long.class)
@ActiveJdbcRequiredProperty(field = "SHORT_VALUE", clazz = Short.class)
@ActiveJdbcRequiredProperty(field = "FIRST_NAME", clazz = String.class)
@ActiveJdbcRequiredProperty(field = "TIME_VALUE", clazz = Time.class)
@ActiveJdbcRequiredProperty(field = "TIMESTAMP_VALUE", clazz = Timestamp.class)
@ActiveJdbcRequiredProperty(field = "CLOB_VALUE_FOR_STRING", clazz = String.class)
public class FancyTable extends Model {
}
```
3. Compile your project for the first time.

That's it. In the same package where your ActiveJDBC classes are placed (in the same package, but folder would be target/generated-sources/annotations ) you'll find new classes with suffix "Wrapper" (in case of FancyTable.java you'll find FancyTableWrapper.java).

4. In case if you want to get a real ActiveJDBC object from this wrapper, you'll have to use ActivejdbcWrapperManipulator (for calling insert method, for example).
```java
    private final ActivejdbcWrapperManipulator manipulator = new ActivejdbcWrapperManipulator();

    @Test
    public void get_real_activejdbc_object() {
        FancyTableWrapper fancyTableWrapper = new FancyTableWrapper();
        FancyTable activejdbcObject = manipulator.getActivejdbcObject(fancyTableWrapper);
        activejdbcObject.insert();
    }
```

That's pretty it :) 
Enjoy!

# License

Apache License
Version 2.0, January 2004
http://www.apache.org/licenses/
