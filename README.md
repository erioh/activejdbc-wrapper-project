# activejdbc-wrapper-project

The main goal of this project is to simplify the usage of ActiveJDBC by generating wrapper classes.

You may have noticed that ActiveJDBC code sometimes looks a bit ugly. On top of that, you have to remember the names
and types of all the fields.

If you use ActiveJDBC in your e2e/integration tests, you can't write simple assertions. You're forced to call a ton of
getters to fetch the data you need. You may also really miss the equals and toString methods, and you don't want to
write them yourself.

With this project, instead of this:

```java
        FancyTable activejdbcObject=new FancyTable();
        activejdbcObject.setString("FIRST_NAME","Some value");
        activejdbcObject.setBigDecimal("AMOUNT",BigDecimal.TEN);
        // 100500 billion other setters
        activejdbcObject.setBoolean("ACTIVATED",false);
```

you can write this:

```java
        FancyTableWrapper fancyTableWrapper=new FancyTableWrapper();
        fancyTableWrapper.setFirstName("Some value");
        fancyTableWrapper.setAmount(BigDecimal.TEN);
        fancyTableWrapper.setActivated(false);
```

If you prefer builders, you can use them as well :)

```java
        SomeTableWrapper someTableWrapper=SomeTableWrapper.builder()
        .someId(1L)
        .aaa("some value")
        .build();
```

All setters and getters are generated automatically. The equals/hashCode/toString methods are overridden automatically
as well, so they are ready to use.

# So, what do you need to get started?

0. Let's assume that you already have some ActiveJDBC classes, everything works, and you only want to simplify their
   usage.

```java

@Table("FANCY_TABLE")
@IdName("FANCY_TABLE_ID")
public class FancyTable extends Model {
}
```

1. Add the dependency to your pom.xml file.

```xml

<dependency>
    <groupId>io.github.erioh</groupId>
    <artifactId>activejdbc-wrapper</artifactId>
    <version>1.0.4</version>
</dependency>
```

2. Add the ActiveJdbcRequiredProperty annotation with the column name and the expected type.

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
@ActiveJdbcRequiredProperty(columnName = "FANCY_TABLE_ID", clazz = Long.class)
@ActiveJdbcRequiredProperty(columnName = "AMOUNT", clazz = BigDecimal.class)
@ActiveJdbcRequiredProperty(columnName = "ACTIVATED", clazz = Boolean.class)
@ActiveJdbcRequiredProperty(columnName = "SQL_DATE", clazz = Date.class)
@ActiveJdbcRequiredProperty(columnName = "LOCAL_DATE_TIME", clazz = LocalDateTime.class)
@ActiveJdbcRequiredProperty(columnName = "LOCAL_DATE", clazz = LocalDate.class)
@ActiveJdbcRequiredProperty(columnName = "LOCAL_TIME", clazz = LocalTime.class)
@ActiveJdbcRequiredProperty(columnName = "DOUBLE_VALUE", clazz = Double.class)
@ActiveJdbcRequiredProperty(columnName = "FLOAT_VALUE", clazz = Float.class)
@ActiveJdbcRequiredProperty(columnName = "INTEGER_VALUE", clazz = Integer.class)
@ActiveJdbcRequiredProperty(columnName = "LONG_VALUE", clazz = Long.class)
@ActiveJdbcRequiredProperty(columnName = "SHORT_VALUE", clazz = Short.class)
@ActiveJdbcRequiredProperty(columnName = "FIRST_NAME", clazz = String.class)
@ActiveJdbcRequiredProperty(columnName = "TIME_VALUE", clazz = Time.class)
@ActiveJdbcRequiredProperty(columnName = "TIMESTAMP_VALUE", clazz = Timestamp.class)
@ActiveJdbcRequiredProperty(columnName = "CLOB_VALUE_FOR_STRING", clazz = String.class)
public class FancyTable extends Model {
}
```

Types with dedicated accessors: `String`, `Boolean`, `Short`, `Integer`, `Long`, `Float`, `Double`, `BigDecimal`,
`BigInteger`, `byte[]`, `java.sql.Date`, `java.sql.Time`, `java.sql.Timestamp`, `java.util.Date`,
`LocalDate`, `LocalTime`, `LocalDateTime` and `Instant`. Any other non-primitive type is read with a plain cast of
`get(...)` and written with `set(...)`.

For CLOB columns, `clazz = String.class` is usually the best choice: `getString(...)` reads the whole text. With
`clazz = java.sql.Clob.class` the getter returns the driver's `Clob`, which typically can only be read while the
connection is open, and models marked with `@Cached` hold CLOBs as `String`, so the cast fails for them.

3. Compile your project for the first time.

That's it. You'll find new classes with the suffix "Wrapper" in the same package as your ActiveJDBC classes, but in the
target/generated-sources/annotations folder (for FancyTable.java, it's FancyTableWrapper.java). For a static nested
model `Outer.Inner`, the wrapper is called `Outer_InnerWrapper`.

> **JDK 23 and newer:** javac no longer runs annotation processors that are only found on the classpath, so with
> nothing but the dependency from step 1 no wrapper is generated, and there is no error either. Enable the processor
> explicitly, for example with `maven-compiler-plugin`:
>
> ```xml
> <plugin>
>     <groupId>org.apache.maven.plugins</groupId>
>     <artifactId>maven-compiler-plugin</artifactId>
>     <configuration>
>         <annotationProcessorPaths>
>             <path>
>                 <groupId>io.github.erioh</groupId>
>                 <artifactId>activejdbc-wrapper</artifactId>
>                 <version>1.0.4</version>
>             </path>
>         </annotationProcessorPaths>
>     </configuration>
> </plugin>
> ```
>
> Listing the processor in `<annotationProcessors>` (as in the example in step 6) or passing `-proc:full` to javac works
> as well.
> Gradle's `annotationProcessor` configuration needs nothing extra.

Query results can be wrapped in one go, which is handy for assertions on whole lists:

```java
        List<FancyTableWrapper> rows = FancyTableWrapper.wrap(FancyTable.findAll());
        assertThat(rows).containsExactly(expectedFirst, expectedSecond);
```

Each wrapper also has a constant per column, handy for queries without string literals:

```java
        List<FancyTable> rows = FancyTable.where(FancyTableWrapper.FIRST_NAME + " = ?", "Some value");
```

The constant name is the column name in upper case, with characters that can't be part of a Java identifier
replaced by `_`.

4. If you need the real ActiveJDBC object behind a wrapper (to call the insert method, for example), use
   ActivejdbcWrapperManipulator.

```java
    private final ActivejdbcWrapperManipulator manipulator=new ActivejdbcWrapperManipulator();

@Test
public void get_real_activejdbc_object(){
        FancyTableWrapper fancyTableWrapper=new FancyTableWrapper();
        FancyTable activejdbcObject=manipulator.getActivejdbcObject(fancyTableWrapper);
        activejdbcObject.insert();
        }
```

5. If you are not happy with the default suffix, you can easily override it with your own suffix using the
   `activejdbc.wrapper.suffix` option.

6. If you want to change the names of the builder methods, you can specify your own prefix using the
   `activejdbc.wrapper.builder.method.prefix` option. Both options are passed to the compiler as shown below.
```java
        SomeTableWrapper someTableWrapper=SomeTableWrapper.builder()
        .withSomeId(1L)
        .withAaa("some value")
        .build();
```
```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <encoding>UTF-8</encoding>
        <generatedSourcesDirectory>${project.build.directory}/generated-sources/annotations</generatedSourcesDirectory>
        <annotationProcessors>
            <annotationProcessor>
                activejdbc.wrapper.annotation.processor.ActiveJdbcRequiredPropertyProcessor
            </annotationProcessor>
        </annotationProcessors>
        <compilerArgs>
            <arg>-Aactivejdbc.wrapper.suffix=NewWrapperSuffix</arg>
            <arg>-Aactivejdbc.wrapper.builder.method.prefix=with</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

7. Sometimes two column names map to the same property, for example `ADDRESS` and `_ADDRESS`. The processor reports
   such a collision as a compilation error; set `desiredFieldName` for one of the columns to resolve it.

```java

@ActiveJdbcRequiredProperty(columnName = "ADDRESS", clazz = String.class)
@ActiveJdbcRequiredProperty(columnName = "_ADDRESS", clazz = String.class, desiredFieldName = "anotherAddress")
public class FancyTableWithCustomColumn extends Model {
}
```

That's pretty much it :)
Enjoy!

# License

Apache License Version 2.0, January 2004
http://www.apache.org/licenses/
