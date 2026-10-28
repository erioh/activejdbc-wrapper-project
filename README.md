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
    <version>1.1.0</version>
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
`LocalDate`, `LocalTime`, `LocalDateTime` and `Instant`. Enums are stored by name in a text column (`name()` on
write, `valueOf` on read, also in the query builder). Any other non-primitive type is read with a plain cast of
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
>                 <version>1.1.0</version>
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

Each wrapper has a typed query builder, so conditions are written with property names instead of SQL strings:

```java
        List<FancyTableWrapper> rows = FancyTableWrapper.query()
                .firstName().eq("Some value")
                .and(q -> q.amount().gt(BigDecimal.TEN).or().activated().isNull())
                .fetch();
```

This runs `FIRST_NAME = ? AND (AMOUNT > ? OR ACTIVATED IS NULL)`. The value types are checked by the compiler:
`amount()` only accepts `BigDecimal`.

* Operators: `eq`, `ne`, `gt`, `ge`, `lt`, `le`, `between`, `in`, `notIn`, `isNull`, `isNotNull`, and `like` /
  `notLike` for `String` columns. `eq(null)` means `IS NULL`.
* Conditions are joined with AND unless `or()` is called in between; as in SQL, AND binds tighter than OR.
  `and(q -> ...)`, `or(q -> ...)` and `group(q -> ...)` put conditions in parentheses.
* Sorting and paging: `amount().desc()`, `firstName().asc()` (applied in the order they are added), `limit(n)`,
  `offset(n)`.
* `fetch()` returns wrappers, `fetchModels()` returns ActiveJDBC models (a `LazyList`), `fetchFirst()` returns an
  `Optional` with the first matching wrapper (reading only that row), `fetchCount()` returns the number of matching
  rows (sorting and paging don't apply to it). `getSql()`, `getOrderBy()` and `getParams()` show what will run.
* Queries work with and without ActiveJDBC instrumentation.
* A column whose property name clashes with a method of the query class (for example `TO_STRING` → `toString`)
  is left out of the query builder with a compiler warning; set `desiredFieldName` to use it.

```java
        Optional<FancyTableWrapper> newest = FancyTableWrapper.query()
                .activated().eq(true)
                .fancyTableId().desc()
                .fetchFirst();
```

```java
        List<FancyTableWrapper> page = FancyTableWrapper.query()
                .activated().eq(true)
                .amount().desc().firstName().asc()
                .limit(20).offset(40)
                .fetch();
```

Each wrapper also has a constant per column, for raw SQL:

```java
        List<Map> rows = Base.findAll("SELECT " + FancyTableWrapper.FIRST_NAME + ", COUNT(*) FROM FANCY_TABLE GROUP BY "
                + FancyTableWrapper.FIRST_NAME);
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

# Known limitations

The wrappers were tested against Oracle Free 23ai and Oracle XE 21c (ojdbc11 23.26). These things are outside what a
wrapper can fix: they come from ActiveJDBC 2.2, the JDBC driver or the database itself.

**Types**

- `TIMESTAMP WITH TIME ZONE` and `TIMESTAMP WITH LOCAL TIME ZONE` (Oracle) can be written but not read: ActiveJDBC
  hands over the driver's own objects, which neither `Timestamp` nor `OffsetDateTime`/`LocalDateTime` getters can
  convert. The same goes for `INTERVAL` columns mapped to `Duration`.
- `UUID` works only where the driver supports it (H2 does); on Oracle map `RAW(16)` as `byte[]` or keep UUIDs in
  `VARCHAR2(36)` as `String`.
- `Boolean` needs a numeric column (`NUMBER(1)`) or a native `BOOLEAN`: the Oracle driver writes `true`/`false` into a
  `CHAR(1)`, which doesn't fit. Reading `'Y'`/`'N'`, `'T'`/`'F'` and `'1'`/`'0'` works.
- Oracle's driver binds `Float`/`Double` as `NUMBER`, so `NaN`, infinities and doubles beyond `NUMBER`'s range
  (about 1e-130 to 1e126) can't be written to `BINARY_FLOAT`/`BINARY_DOUBLE` columns.
- `LocalDateTime`, `LocalDate`, `LocalTime` and `Instant` go through `java.sql.Timestamp`/`Date`/`Time` in the JVM
  time zone. A local time skipped by a DST change moves an hour forward, two instants in the hour repeated when DST
  ends can't be told apart in a column without a zone, and dates around the 1582 calendar switch shift. Running the
  JVM in UTC avoids the DST cases.

**Queries**

- Oracle can't compare or sort `CLOB`/`NCLOB`/`BLOB` columns: the query builder offers `eq`, `in`, `asc()` and so on
  for them, but the database rejects those (ORA-22848). `isNull()`, `isNotNull()` and `like()` work.
- Oracle stores `""` as `NULL`, and `CHAR(n)` values come back blank-padded; `eq("abc")` doesn't match a `CHAR`
  value `'abc       '`.

**ActiveJDBC setup**

- One model per table: a second model with the same `@Table` replaces the first one in ActiveJDBC's registry, which
  then fails with `Failed to find table`. Use a view for an alternative mapping.
- On Oracle, saving a model with every attribute `null` runs `INSERT INTO T (ID) VALUES (NULL)`: give the id column
  `GENERATED BY DEFAULT ON NULL AS IDENTITY` or an `@IdGenerator` sequence.
- The `activejdbc-instrumentation` 2.2 Maven plugin fails on JDK 17+ unless Maven runs with
  `--add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED`
  (for example in `.mvn/jvm.config`).
- Oracle XE 21c doesn't know the time zone name `Europe/Kyiv` (only the old `Europe/Kiev`) and refuses the
  connection with ORA-01882; `-Doracle.jdbc.timezoneAsRegion=false` makes the driver send an offset instead.
- ojdbc 23.26 corrupts `CLOB`/`NCLOB` text longer than 32767 characters when an emoji (or any surrogate pair) spans
  position 32767: it is stored with U+FFFD in its place. Plain JDBC does the same.

# License

Apache License Version 2.0, January 2004
http://www.apache.org/licenses/
