package activejdbc.wrapper.annotation.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


@RunWith(DataProviderRunner.class)
public class ActiveJdbcRequiredPropertyProcessorTest {

    @Test
    public void should_create_new_class() throws IOException {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forResource("FancyTable.java");
        // when
        Compilation compilation =
                javac()
                        .withProcessors(new ActiveJdbcRequiredPropertyProcessor())
                        .compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("com.sdemenkov.FancyTableWrapper")
                .hasSourceEquivalentTo(expectedWrapper("FancyTableWrapper"));
    }

    @Test
    public void should_create_new_class_with_options() throws IOException {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forResource("FancyTable.java");
        // when
        Compilation compilation =
                javac()
                        .withProcessors(new ActiveJdbcRequiredPropertyProcessor())
                        .withOptions("-Aactivejdbc.wrapper.suffix=NewWrapperSuffix", "-Aactivejdbc.wrapper.builder.method.prefix=with")
                        .compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("com.sdemenkov.FancyTableNewWrapperSuffix")
                .hasSourceEquivalentTo(expectedWrapper("FancyTableNewWrapperSuffix"));
    }

    @Test
    public void should_create_new_class_with_custom_column() throws IOException {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forResource("FancyTableWithCustomColumn.java");
        // when
        Compilation compilation =
                javac()
                        .withProcessors(new ActiveJdbcRequiredPropertyProcessor())
                        .compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("com.sdemenkov.FancyTableWithCustomColumnWrapper")
                .hasSourceEquivalentTo(expectedWrapper("FancyTableWithCustomColumnWrapper"));
    }

    @Test
    public void should_create_new_class_with_custom_column_and_options() throws IOException {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forResource("FancyTableWithCustomColumn.java");
        // when
        Compilation compilation =
                javac()
                        .withProcessors(new ActiveJdbcRequiredPropertyProcessor())
                        .withOptions("-Aactivejdbc.wrapper.suffix=NewWrapperSuffix", "-Aactivejdbc.wrapper.builder.method.prefix=with")
                        .compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("com.sdemenkov.FancyTableWithCustomColumnNewWrapperSuffix")
                .hasSourceEquivalentTo(expectedWrapper("FancyTableWithCustomColumnNewWrapperSuffix"));
    }

    @DataProvider
    public static Object[][] data_for_should_fail_on_illegal_options() {
        return new Object[][]{
                {"-Aactivejdbc.wrapper.suffix=NewWrapper*Suffix",
                        "Custom suffix has illegal characters. " +
                                "Please use only characters in uppercase and lowercase, underscore, and numbers."},
                {"-Aactivejdbc.wrapper.builder.method.prefix=with*Something",
                        "Custom prefix has illegal characters. " +
                                "Please use only characters in uppercase and lowercase, underscore, and numbers."},
        };
    }

    @Test
    @UseDataProvider("data_for_should_fail_on_illegal_options")
    public void should_fail_on_illegal_options(String options, String message) {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forResource("FancyTable.java");
        // when
        Compilation compilation = javac()
                .withProcessors(new ActiveJdbcRequiredPropertyProcessor())
                .withOptions(options)
                .compile(javaFileObject);
        // then
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining(message);
    }

    @Test
    public void should_create_wrappers_for_classes_with_same_name_in_different_packages() {
        // given
        JavaFileObject first = model("a.User", "@ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class)");
        JavaFileObject second = model("b.User", "@ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class)");
        // when
        Compilation compilation = compile(first, second);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("a.UserWrapper");
        assertThat(compilation).generatedSourceFile("b.UserWrapper");
    }

    @DataProvider
    public static Object[][] data_for_should_support_types_unknown_to_processor() {
        return new Object[][]{
                {"byte[].class"},
                {"java.util.Map.Entry.class"},
                {"Color.class"},
        };
    }

    @Test
    @UseDataProvider("data_for_should_support_types_unknown_to_processor")
    public void should_support_types_unknown_to_processor(String clazz) {
        // given
        JavaFileObject color = JavaFileObjects.forSourceString("e.Color", "package e; public enum Color { RED }");
        JavaFileObject car = model("e.Car", "@ActiveJdbcRequiredProperty(columnName = \"VALUE\", clazz = " + clazz + ")");
        // when
        Compilation compilation = compile(color, car);
        // then
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("e.CarWrapper");
    }

    @Test
    public void should_use_dedicated_accessors_for_supported_types() throws IOException {
        // given
        JavaFileObject javaFileObject = model("t.Typed",
                "@ActiveJdbcRequiredProperty(columnName = \"CONTENT\", clazz = byte[].class)\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"CREATED\", clazz = java.util.Date.class)\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"UPDATED\", clazz = java.time.Instant.class)\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"COUNTER\", clazz = java.math.BigInteger.class)");
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        String source = compilation.generatedSourceFile("t.TypedWrapper").get().getCharContent(true).toString();
        Assertions.assertThat(source)
                .contains("typed.getBytes(\"CONTENT\")")
                .contains("typed.getTimestamp(\"CREATED\")")
                .contains("this.typed.setTimestamp(\"CREATED\", created)")
                .contains("typed.getTimestamp(\"UPDATED\"))")
                .contains("this.typed.setTimestamp(\"UPDATED\", java.util.Optional.ofNullable(updated).map(java.sql.Timestamp::from)")
                .contains("typed.getBigDecimal(\"COUNTER\"))")
                .contains("this.typed.setBigDecimal(\"COUNTER\", java.util.Optional.ofNullable(counter).map(java.math.BigDecimal::new)")
                // arrays are compared by content
                .contains("java.util.Objects.deepEquals(this.getContent(), that.getContent())")
                .contains("java.util.Arrays.deepHashCode(new java.lang.Object[]{");
    }

    @Test
    public void should_create_wrapper_for_class_in_default_package() {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forSourceString("Plain",
                "import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;\n" +
                        "import org.javalite.activejdbc.Model;\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class)\n" +
                        "public class Plain extends Model {}");
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("PlainWrapper");
    }

    @Test
    public void should_print_arrays_readably_in_to_string() throws Exception {
        // given
        JavaFileObject model = model("a.Blob", "@ActiveJdbcRequiredProperty(columnName = \"CONTENT\", clazz = byte[].class)\n" +
                "@ActiveJdbcRequiredProperty(columnName = \"TAGS\", clazz = String[].class)\n" +
                "@ActiveJdbcRequiredProperty(columnName = \"MATRIX\", clazz = int[][].class)\n" +
                "@ActiveJdbcRequiredProperty(columnName = \"EMPTY\", clazz = byte[].class)");
        // when
        Compilation compilation = compile(model);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        String source = compilation.generatedSourceFile("a.BlobWrapper").get().getCharContent(true).toString();
        Assertions.assertThat(source)
                .contains("\"<\" + this.getContent().length + \" bytes>\"")
                .contains("java.util.Arrays.toString(this.getTags())")
                .contains("java.util.Arrays.deepToString(this.getMatrix())")
                .contains("this.getEmpty() == null ? null");
    }

    @Test
    public void should_create_wrapper_when_property_name_equals_activejdbc_object_name() {
        // given
        JavaFileObject javaFileObject = model("s.Status", "@ActiveJdbcRequiredProperty(columnName = \"STATUS\", clazz = String.class)");
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
    }

    @DataProvider
    public static Object[][] data_for_should_fail_on_invalid_property_name() {
        return new Object[][]{
                {"@ActiveJdbcRequiredProperty(columnName = \"DEFAULT\", clazz = Boolean.class)",
                        "Column [DEFAULT] produces property name [default], which is not a valid Java identifier. Please set desiredFieldName for this column."},
                {"@ActiveJdbcRequiredProperty(columnName = \"_\", clazz = String.class)",
                        "Column [_] produces property name [], which is not a valid Java identifier. Please set desiredFieldName for this column."},
                {"@ActiveJdbcRequiredProperty(columnName = \"FLAG\", clazz = Boolean.class, desiredFieldName = \"class\")",
                        "desiredFieldName [class] of column [FLAG] is not a valid Java identifier."},
                {"@ActiveJdbcRequiredProperty(columnName = \"FLAG\", clazz = boolean.class)",
                        "Column [FLAG] has primitive type [boolean], which is not supported. Please use its wrapper class instead."},
        };
    }

    @Test
    @UseDataProvider("data_for_should_fail_on_invalid_property_name")
    public void should_fail_on_invalid_property_name(String annotation, String message) {
        // given
        JavaFileObject javaFileObject = model("k.Flag", annotation);
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining(message);
        assertThat(compilation).hadErrorCount(1);
    }

    @DataProvider
    public static Object[][] data_for_should_fail_on_property_name_collision() {
        return new Object[][]{
                {"@ActiveJdbcRequiredProperty(columnName = \"ADDRESS\", clazz = String.class)\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"_ADDRESS\", clazz = String.class)",
                        "Columns [ADDRESS] and [_ADDRESS] both produce property name [address]. Please set a different desiredFieldName for one of them."},
                {"@ActiveJdbcRequiredProperty(columnName = \"ADDRESS\", clazz = String.class)\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"STREET\", clazz = String.class, desiredFieldName = \"Address\")",
                        "Columns [ADDRESS] and [STREET] both produce property name [Address]. Please set a different desiredFieldName for one of them."},
                {"@ActiveJdbcRequiredProperty(columnName = \"ADDRESS\", clazz = String.class)\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"ADDRESS\", clazz = String.class)",
                        "Column [ADDRESS] is declared more than once."},
        };
    }

    @Test
    @UseDataProvider("data_for_should_fail_on_property_name_collision")
    public void should_fail_on_property_name_collision(String annotations, String message) {
        // given
        JavaFileObject javaFileObject = model("c.Customer", annotations);
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining(message);
        assertThat(compilation).hadErrorCount(1);
    }

    @Test
    public void should_wrap_collections_of_models_including_lazy_lists() {
        // given
        JavaFileObject model = model("w.Person", "@ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class)");
        JavaFileObject user = JavaFileObjects.forSourceString("w.Usage",
                "package w;\n" +
                        "import java.util.List;\n" +
                        "import org.javalite.activejdbc.LazyList;\n" +
                        "public class Usage {\n" +
                        "    List<PersonWrapper> fromList(List<Person> people) { return PersonWrapper.wrap(people); }\n" +
                        "    List<PersonWrapper> fromQuery(LazyList<Person> people) { return PersonWrapper.wrap(people); }\n" +
                        "}");
        // when
        Compilation compilation = compile(model, user);
        // then
        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    public void should_create_wrapper_when_desired_field_name_resolves_collision() {
        // given
        JavaFileObject javaFileObject = model("c.Customer", "@ActiveJdbcRequiredProperty(columnName = \"ADDRESS\", clazz = String.class)\n" +
                "@ActiveJdbcRequiredProperty(columnName = \"_ADDRESS\", clazz = String.class, desiredFieldName = \"anotherAddress\")");
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    public void should_generate_members_in_annotation_order() throws IOException {
        // given
        String[] columns = {"ZULU", "ALPHA", "MIKE", "BRAVO", "YANKEE", "CHARLIE"};
        StringBuilder annotations = new StringBuilder();
        for (String column : columns) {
            annotations.append(String.format("@ActiveJdbcRequiredProperty(columnName = \"%s\", clazz = String.class)%n", column));
        }
        JavaFileObject javaFileObject = model("o.Ordered", annotations.toString());
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        String source = compilation.generatedSourceFile("o.OrderedWrapper").get().getCharContent(true).toString();
        for (String member : new String[]{"public java.lang.String get%s()", "public void set%s(", "%s = \"", "this.get%s(), that.get%s()", "public OrderedWrapperBuilder %s("}) {
            int previous = -1;
            for (String column : columns) {
                String propertyName = column.toLowerCase();
                String name = member.startsWith("public OrderedWrapperBuilder") || member.startsWith("%s =")
                        ? propertyName
                        : Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
                int index = source.indexOf(member.replace("%s", name));
                Assertions.assertThat(index).as("%s for column %s", member, column).isGreaterThan(previous);
                previous = index;
            }
        }
    }

    @Test
    public void should_create_wrapper_when_desired_field_name_resolves_keyword_column() {
        // given
        JavaFileObject javaFileObject = model("k.Flag", "@ActiveJdbcRequiredProperty(columnName = \"DEFAULT\", clazz = Boolean.class, desiredFieldName = \"defaultValue\")");
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
    }

    @DataProvider
    public static Object[][] data_for_should_fail_when_class_is_not_an_instantiable_model() {
        return new Object[][]{
                {"public class Pojo {}",
                        "Class [x.Pojo] must extend org.javalite.activejdbc.Model to be annotated with ActiveJdbcRequiredProperty."},
                {"public abstract class Abstract extends Model {}",
                        "Class [x.Abstract] is abstract, but the wrapper needs to create its instances."},
                {"public class PrivateConstructor extends Model { private PrivateConstructor() {} }",
                        "Class [x.PrivateConstructor] needs a non-private constructor without parameters, the wrapper uses it to create instances."},
                {"public class OnlyArgsConstructor extends Model { public OnlyArgsConstructor(String name) {} }",
                        "Class [x.OnlyArgsConstructor] needs a non-private constructor without parameters, the wrapper uses it to create instances."},
        };
    }

    @Test
    @UseDataProvider("data_for_should_fail_when_class_is_not_an_instantiable_model")
    public void should_fail_when_class_is_not_an_instantiable_model(String declaration, String message) {
        // given
        String className = declaration.replaceAll(".*class (\\w+).*", "$1");
        JavaFileObject javaFileObject = JavaFileObjects.forSourceString("x." + className,
                "package x;\n" +
                        "import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;\n" +
                        "import org.javalite.activejdbc.Model;\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class)\n" +
                        declaration);
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining(message);
        assertThat(compilation).hadErrorCount(1);
    }

    @Test
    public void should_create_wrapper_for_model_with_package_private_constructor() {
        // given
        JavaFileObject withConstructor = JavaFileObjects.forSourceString("x.Hidden",
                "package x;\n" +
                        "import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;\n" +
                        "import org.javalite.activejdbc.Model;\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class)\n" +
                        "public class Hidden extends Model { Hidden() {} }");
        // when
        Compilation compilation = compile(withConstructor);
        // then
        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    public void should_create_wrappers_for_static_nested_classes() {
        // given: two nested classes with the same simple name in one package
        JavaFileObject first = source("n.First", "public class First { @ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class) public static class Item extends Model {} }");
        JavaFileObject second = source("n.Second", "public class Second { @ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class) public static class Item extends Model {} }");
        // when
        Compilation compilation = compile(first, second);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("n.First_ItemWrapper");
        assertThat(compilation).generatedSourceFile("n.Second_ItemWrapper");
    }

    @Test
    public void should_create_wrapper_for_nested_class_in_default_package() {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forSourceString("Outer",
                "import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;\n" +
                        "import org.javalite.activejdbc.Model;\n" +
                        "public class Outer { @ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class) public static class Inner extends Model {} }");
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("Outer_InnerWrapper");
    }

    @DataProvider
    public static Object[][] data_for_should_fail_when_class_is_not_reachable_from_wrapper() {
        return new Object[][]{
                {"public class Outer { @ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class) public class Inner extends Model {} }",
                        "Class [n.Outer.Inner] is an inner class, make it static to be annotated with ActiveJdbcRequiredProperty."},
                {"public class Outer { @ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class) private static class Inner extends Model {} }",
                        "Class [n.Outer.Inner] is private, the generated wrapper can't access it."},
                {"public class Outer { private static class Middle { @ActiveJdbcRequiredProperty(columnName = \"NAME\", clazz = String.class) static class Inner extends Model {} } }",
                        "Class [n.Outer.Middle] is private, the generated wrapper can't access it."},
        };
    }

    @Test
    @UseDataProvider("data_for_should_fail_when_class_is_not_reachable_from_wrapper")
    public void should_fail_when_class_is_not_reachable_from_wrapper(String declaration, String message) {
        // given
        JavaFileObject javaFileObject = source("n.Outer", declaration);
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining(message);
        assertThat(compilation).hadErrorCount(1);
    }

    @Test
    public void should_fail_when_interface_is_annotated() {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forSourceString("i.Api",
                "package i; import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;\n" +
                        "@ActiveJdbcRequiredProperty(columnName = \"X\", clazz = String.class) public interface Api {}");
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Only classes can be annotated with ActiveJdbcRequiredProperty");
    }

    private static Compilation compile(JavaFileObject... javaFileObjects) {
        return javac()
                .withProcessors(new ActiveJdbcRequiredPropertyProcessor())
                .compile(javaFileObjects);
    }

    private static JavaFileObject model(String qualifiedName, String annotation) {
        int lastDot = qualifiedName.lastIndexOf('.');
        return JavaFileObjects.forSourceString(qualifiedName,
                "package " + qualifiedName.substring(0, lastDot) + ";\n" +
                        "import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;\n" +
                        "import org.javalite.activejdbc.Model;\n" +
                        annotation + "\n" +
                        "public class " + qualifiedName.substring(lastDot + 1) + " extends Model {}");
    }

    /**
     * Expected wrappers are written with javax.annotation.processing.Generated, which only exists since Java 9;
     * on Java 8 the processor falls back to javax.annotation.Generated.
     */
    private static JavaFileObject expectedWrapper(String className) throws IOException {
        String source = JavaFileObjects.forResource(className + ".java").getCharContent(true).toString();
        if (System.getProperty("java.specification.version").startsWith("1.")) {
            source = source.replace("@javax.annotation.processing.Generated(", "@javax.annotation.Generated(");
        }
        return JavaFileObjects.forSourceString("com.sdemenkov." + className, source);
    }

    private static JavaFileObject source(String qualifiedName, String declaration) {
        int lastDot = qualifiedName.lastIndexOf('.');
        return JavaFileObjects.forSourceString(qualifiedName,
                "package " + qualifiedName.substring(0, lastDot) + ";\n" +
                        "import activejdbc.wrapper.annotation.ActiveJdbcRequiredProperty;\n" +
                        "import org.javalite.activejdbc.Model;\n" +
                        declaration);
    }
}
