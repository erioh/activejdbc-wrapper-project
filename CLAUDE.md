# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Java annotation processor (published as `io.github.erioh:activejdbc-wrapper`) that generates typed wrapper classes for ActiveJDBC models. A user annotates a `Model` subclass with repeatable `@ActiveJdbcRequiredProperty(columnName, clazz, desiredFieldName)`, and at compile time the processor emits `<ModelName>Wrapper` with typed getters/setters, `equals`/`hashCode`/`toString`, a builder, and a protected `getActivejdbcObject()` (reachable from outside via `ActivejdbcWrapperManipulator`). README.md has the end-user documentation.

## Build and test

Toolchain is installed via SDKMAN (`source ~/.sdkman/bin/sdkman-init.sh`). Code targets Java 8 and must keep building on JDK 8; it is also verified on the latest JDK. Switch per shell with `sdk use java 8.0.504.0+1-tem` / `sdk use java 27.0.0.0+35-tem`.

```
mvn test                                   # all tests
mvn test -Dtest=StringUtilsTest            # single test class
mvn test -Dtest=StringUtilsTest#methodName # single test method
mvn package                                # jar + sources + javadoc jars
mvn install -Dgpg.skip                     # verify/install sign with GPG; skip locally (no key on this machine)
```

When changing anything that affects compilation or tests, run `mvn test` on both JDK 8 and the latest JDK.

### pom.xml quirks
- **Two-pass compile**: `default-compile` compiles *only* `ActiveJdbcRequiredPropertyProcessor.java` with `-proc:none`, then `compile-project` compiles everything else. This is needed because `META-INF/services/javax.annotation.processing.Processor` registers the processor, and javac would otherwise try to load it while compiling this very project.
- Profile `jdk9plus-compile-testing` (auto-activated on JDK 9+) adds `--add-exports jdk.compiler/...` to surefire, because compile-testing reaches into javac internals. Not needed and not active on JDK 8.
- No linter or formatter is configured.

## Architecture

Pipeline (all under `src/main/java/activejdbc/wrapper/annotation/processor/`):

1. `ActiveJdbcRequiredPropertyProcessor` — the `AbstractProcessor`. Reads options `activejdbc.wrapper.suffix` (default `Wrapper`) and `activejdbc.wrapper.builder.method.prefix` (default empty), validated by `StringUtils.isValid`. Collects the annotation mirrors (handling both a single annotation and the `@ActiveJdbcRequiredProperties` container) and writes the generated source through `Filer`.
2. `context/AnnotationProcessorContext` — hand-wired "DI container": builds the type→strategy maps for getters and setters and holds the suffix/prefix. `ColumnContext` = one annotated column (clazz as FQN string, columnName, desiredFieldName).
3. `ActiveJdbcRequiredPropertyWrapperFactory` — turns mirrors into `ColumnContext`s and drives `WrapperClassBuilder` in a fixed chain (`withSetters → withGetters → withMethodGetActivejdbcObject → withToString → withEquals → withHashCode → withBuilder → buildClassBody`). `withGetters` must run before toString/equals/hashCode, since they read the property→getter map it fills.
4. `adapter/builder/WrapperClassBuilder` + `strategy/WrapperBuilderClassBuilder` — generate source by `String.format` on templates (`CLASS_TEMPLATE`, `util/StringTemplates`). There is no JavaPoet; output is raw strings with `%n`.
5. `strategy/getter/*` and `strategy/setter/*` — one strategy per Java type, each supplying a format template that calls the matching ActiveJDBC accessor (`getDate`, `getBigDecimal`, …). `GetterBuilderStrategyHolder`/`SetterBuilderStrategyHolder` look the strategy up by canonical type name and fall back to the `Default*` strategy (plain cast `get`/`set`). Never use `Class.forName` on the column type: it comes from the code being compiled (user enums, arrays, nested classes) and is not on the processor classpath.

Property names: `ColumnContext.getPropertyName()` is the single source of truth — `desiredFieldName` if set, otherwise `StringUtils.buildPropertyNameFromColumnName` (`FIRST_NAME` → `firstName`). The processor validates columns before generating (property name must be a Java identifier and not a keyword; primitive types are rejected) and reports problems via `Messager` errors on the annotated element — don't throw exceptions from the processor. Generated setters reference the ActiveJDBC object as `this.<field>` because a property can have the same name as that field.

### Adding support for a new column type
Add a getter (and, if needed, setter) strategy class, then register it **in `AnnotationProcessorContext`**, which is what is actually used at runtime. The `META-INF/services/...GetterBuilderStrategy` / `...SetterBuilderStrategy` files list the strategies too, but nothing loads them via `ServiceLoader`; keep them in sync anyway. Add a matching `*StrategyTest`.

## Tests

- `ActiveJdbcRequiredPropertyProcessorTest` is end-to-end: it compiles `src/test/resources/FancyTable*.java` with google compile-testing and compares the output against the expected `*Wrapper.java` / `*NewWrapperSuffix.java` sources via `hasSourceEquivalentTo` (AST comparison).
- `WrapperClassBuilderTest` / `WrapperBuilderClassBuilderTest` compare generated text **exactly** with `src/test/resources/expected_*.txt` (loaded through `test/util/ContentExtractor`, joined with the platform line separator).
- Generated members come from `HashSet`/`HashMap` iteration in `WrapperClassBuilder`, so changing those collections or property names can reorder output and break the fixtures. If you change generated code, update the expected resources.
- Test stack: JUnit 4, junit-dataprovider, AssertJ, Mockito 3.
