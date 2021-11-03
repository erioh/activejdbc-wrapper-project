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
        assertThat(compilation).generatedSourceFile("FancyTableWrapper")
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
        assertThat(compilation).generatedSourceFile("FancyTableNewWrapperSuffix")
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
        assertThat(compilation).generatedSourceFile("FancyTableWithCustomColumnWrapper")
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
        assertThat(compilation).generatedSourceFile("FancyTableWithCustomColumnNewWrapperSuffix")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("FancyTableWithCustomColumnNewWrapperSuffix.java"));
    }

    @DataProvider
    public static Object[][] data_for_should_throw_exception_on_illegal_options() {
        return new Object[][]{
                {"-Aactivejdbc.wrapper.suffix=NewWrapper*Suffix",
                        "java.lang.IllegalArgumentException: Custom suffix has illegal characters. " +
                                "Please use only characters in uppercase and lowercase, underscore, and numbers."},
                {"-Aactivejdbc.wrapper.builder.method.prefix=with*Something",
                        "java.lang.IllegalArgumentException: Custom prefix has illegal characters. " +
                                "Please use only characters in uppercase and lowercase, underscore, and numbers."},
        };
    }

    @Test
    @UseDataProvider("data_for_should_throw_exception_on_illegal_options")
    public void should_throw_exception_on_illegal_options(String options, String message) {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forResource("FancyTable.java");
        // when | then

        Assertions.assertThatThrownBy(() -> javac()
                .withProcessors(new ActiveJdbcRequiredPropertyProcessor())
                .withOptions(options)
                .compile(javaFileObject))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(message);
    }
}