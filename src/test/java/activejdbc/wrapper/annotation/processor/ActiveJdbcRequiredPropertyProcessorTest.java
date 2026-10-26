package activejdbc.wrapper.annotation.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


@RunWith(DataProviderRunner.class)
public class ActiveJdbcRequiredPropertyProcessorTest {

    @Test
    public void should_create_new_class() {
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
                .hasSourceEquivalentTo(JavaFileObjects.forResource("FancyTableWrapper.java"));
    }

    @Test
    public void should_create_new_class_with_options() {
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
                .hasSourceEquivalentTo(JavaFileObjects.forResource("FancyTableNewWrapperSuffix.java"));
    }

    @Test
    public void should_create_new_class_with_custom_column() {
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
                .hasSourceEquivalentTo(JavaFileObjects.forResource("FancyTableWithCustomColumnWrapper.java"));
    }

    @Test
    public void should_create_new_class_with_custom_column_and_options() {
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
                .hasSourceEquivalentTo(JavaFileObjects.forResource("FancyTableWithCustomColumnNewWrapperSuffix.java"));
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
    public void should_create_wrapper_when_desired_field_name_resolves_keyword_column() {
        // given
        JavaFileObject javaFileObject = model("k.Flag", "@ActiveJdbcRequiredProperty(columnName = \"DEFAULT\", clazz = Boolean.class, desiredFieldName = \"defaultValue\")");
        // when
        Compilation compilation = compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
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
}
