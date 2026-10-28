# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Java annotation processor (published as `io.github.erioh:activejdbc-wrapper`) that generates typed wrapper classes for ActiveJDBC models. A user annotates a `Model` subclass with repeatable `@ActiveJdbcRequiredProperty(columnName, clazz, desiredFieldName)`, and at compile time the processor emits `<ModelName>Wrapper` with typed getters/setters, `equals`/`hashCode`/`toString`, a builder, and a protected `getActivejdbcObject()` (reachable from outside via `ActivejdbcWrapperManipulator`). README.md has the end-user documentation.

## Build and test

Toolchain is installed via SDKMAN (`source ~/.sdkman/bin/sdkman-init.sh`). Code targets Java 8 and must keep building on JDK 8; it is also verified on the latest JDK. Switch per shell with `sdk use java 8.0.504.0+1-tem` / `sdk use java 27.0.0.0+35-tem`.

```
mvn test                                   # all tests
mvn test -Dtest=StringUtilsTest            # single test class
mvn test -Dtest=StringUtilsTest#methodName # single test method; NOT for @RunWith(DataProviderRunner) classes —
                                           # the filter silently matches 0 tests there, run the whole class instead
mvn package                                # jar + sources + javadoc jars
mvn install -Dgpg.skip                     # verify/install sign with GPG; skip locally (no key on this machine)
```

When changing anything that affects compilation or tests, run `mvn test` on both JDK 8 and the latest JDK.

### pom.xml quirks
- **`-proc:none` for main sources**: `META-INF/services/javax.annotation.processing.Processor` registers the processor, so without it javac would run the processor on this very project. Its `init()` loads strategies via `ServiceLoader` and fails with `ServiceConfigurationError` when the strategy classes aren't compiled yet.
- Profile `jdk9plus-compile-testing` (auto-activated on JDK 9+) adds `--add-exports jdk.compiler/...` to surefire, because compile-testing reaches into javac internals. Not needed and not active on JDK 8.
- No linter or formatter is configured.

### Releasing
Published to Maven Central through the Central Portal (`central-publishing-maven-plugin`, `autoPublish` on). A release needs a Central Portal user token in the `central` server of `~/.m2/settings.xml` and a GPG key; set a non-SNAPSHOT version, then `mvn clean deploy`. Publishing to Maven Central can't be undone, so never run `deploy` without the maintainer explicitly asking. A dry run that builds and signs everything but uploads nothing: `mvn deploy -DskipPublishing=true` (still needs a `central` server entry, dummy credentials are fine).

## Architecture

Pipeline (all under `src/main/java/activejdbc/wrapper/annotation/processor/`):

1. `ActiveJdbcRequiredPropertyProcessor` — the `AbstractProcessor`. Reads options `activejdbc.wrapper.suffix` (default `Wrapper`) and `activejdbc.wrapper.builder.method.prefix` (default empty), validated by `StringUtils.isValid`. Collects the annotation mirrors (handling both a single annotation and the `@ActiveJdbcRequiredProperties` container) and writes the generated source through `Filer`.
2. `context/AnnotationProcessorContext` — builds the type→strategy maps for getters and setters from `ServiceLoader` (keyed by each strategy's `typesToApply()`, two strategies claiming one type is an error) and holds the suffix/prefix. `ServiceLoader` must get the processor's own class loader: javac/Maven load processors in a separate class loader, and with the default context class loader nothing is found and every column silently falls back to the default strategy (the 1.0.4 "critical bug"). `ColumnContext` = one annotated column (clazz as FQN string, columnName, desiredFieldName).
3. `ActiveJdbcRequiredPropertyWrapperFactory` — turns mirrors into `ColumnContext`s and drives `WrapperClassBuilder` in a fixed chain (`withSetters → withGetters → withMethodGetActivejdbcObject → withToString → withEquals → withHashCode → withBuilder → buildClassBody`). `withGetters` must run before toString/equals/hashCode, since they read the property→getter map it fills.
4. `adapter/builder/WrapperClassBuilder` + `strategy/WrapperBuilderClassBuilder` — generate source by `String.format` on templates (`CLASS_TEMPLATE`, `util/StringTemplates`). There is no JavaPoet; output is raw strings with `%n`.
5. `strategy/getter/*` and `strategy/setter/*` — one strategy per Java type, each supplying a format template that calls the matching ActiveJDBC accessor (`getDate`, `getBigDecimal`, …). `GetterBuilderStrategyHolder`/`SetterBuilderStrategyHolder` look the strategy up by canonical type name and fall back to the `Default*` strategy (plain cast `get`/`set`). Enums can't be keyed by class name, so the factory marks them (`ColumnContext.isEnumType()`, from the annotation's `TypeMirror`) and `WrapperClassBuilder` picks `Enum*BuilderStrategy` for them directly. Date/time getters (`java.sql.Date`, `Time`, `LocalDate`, `LocalTime`) call the runtime helper `activejdbc.wrapper.TemporalReader`, which falls back to `getTimestamp` for driver values ActiveJDBC's `getDate`/`getTime` can't convert (Oracle's `oracle.sql.TIMESTAMP`). Never use `Class.forName` on the column type: it comes from the code being compiled (user enums, arrays, nested classes) and is not on the processor classpath.

Per-column flags from the annotation live in `ColumnContext`: `readOnly` drops the setter and the builder method (the getter, `toString`, `equals` and the query method stay), `excludeFromEqualsAndHashCode` drops the property from `equals`/`hashCode` only.

Property names: `ColumnContext.getPropertyName()` is the single source of truth — `desiredFieldName` if set, otherwise `StringUtils.buildPropertyNameFromColumnName` (`FIRST_NAME` → `firstName`). The processor validates columns before generating (property name must be a Java identifier and not a keyword; primitive types are rejected) and reports problems via `Messager` errors on the annotated element — don't throw exceptions from the processor. Generated setters reference the ActiveJDBC object as `this.<field>` because a property can have the same name as that field.

### Query builder
`FancyTableWrapper.query()` returns a generated nested `<Wrapper>Query` class (`WrapperQueryClassBuilder`) that only declares one method per column; all logic is hand-written runtime code in `activejdbc.wrapper.query` (`AbstractQuery`, `Condition`, `StringCondition`), which ships in the library jar. Sorting (`Condition.asc()/desc()`) and `limit`/`offset` are applied to the resulting `LazyList`; `fetchFirst()` runs the same with limit 1 without changing the query. When adding a public method to `AbstractQuery`, add its name to `RESERVED_METHOD_NAMES`, or a column with that property name breaks the generated query class. Queries run through `ModelDelegate.where/count/findAll` with the model class passed explicitly, so they work without ActiveJDBC instrumentation (and in the H2 tests). `Condition.toJdbc` converts parameters the same way as the setter strategies; keep them in sync when adding a type. Columns whose property name is in `WrapperQueryClassBuilder.RESERVED_METHOD_NAMES` are left out of the query class with a warning.

### Adding support for a new column type
Add a getter (and, if needed, setter) strategy class with `typesToApply()`, list it in `META-INF/services/...GetterBuilderStrategy` / `...SetterBuilderStrategy`, add a matching `*StrategyTest` and a row in `AnnotationProcessorContextTest`.

## Tests

- `ActiveJdbcRequiredPropertyProcessorTest` is end-to-end: it compiles `src/test/resources/FancyTable*.java` with google compile-testing and compares the output against the expected `*Wrapper.java` / `*NewWrapperSuffix.java` sources via `hasSourceEquivalentTo` (AST comparison).
- `WrapperClassBuilderTest` / `WrapperBuilderClassBuilderTest` compare generated text **exactly** with `src/test/resources/expected_*.txt` (loaded through `test/util/ContentExtractor`, joined with the platform line separator).
- Generated members follow annotation order (`LinkedHashSet`/`LinkedHashMap` in `WrapperClassBuilder`; guarded by `should_generate_members_in_annotation_order`). If you change generated code, update the expected resources.
- `integration/GeneratedWrapperIntegrationTest` runs a generated wrapper against H2 in memory: values written through `AllTypesWrapper` must come back with the same type and value. The wrapper is generated during `testCompile` (processor named explicitly in `pom.xml`), and the model is registered in `src/test/resources/activejdbc_models.properties`, so no ActiveJDBC instrumentation is needed — only instance methods (`saveIt`, `refresh`) work, static finders don't. When adding a type, add a column to `AllTypes` and the table.
- Test stack: JUnit 4, junit-dataprovider, AssertJ, Mockito 4 (5+ needs Java 11, Dependabot is told to skip it).
