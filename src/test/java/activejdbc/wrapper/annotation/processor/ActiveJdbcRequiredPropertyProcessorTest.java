package activejdbc.wrapper.annotation.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


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
                        .withOptions("-Aactivejdbc.wrapper.suffix=NewWrapperSuffix")
                        .compile(javaFileObject);
        // then
        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedSourceFile("FancyTableNewWrapperSuffix")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("FancyTableNewWrapperSuffix.java"));
    }

    @Test
    public void should_throw_exception_on_illegal_options() {
        // given
        JavaFileObject javaFileObject = JavaFileObjects.forResource("FancyTable.java");
        // when | then

        Assertions.assertThatThrownBy(() -> javac()
                .withProcessors(new ActiveJdbcRequiredPropertyProcessor())
                .withOptions("-Aactivejdbc.wrapper.suffix=NewWrapper*Suffix")
                .compile(javaFileObject))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("java.lang.IllegalArgumentException: Custom suffix has illegal characters. Please use only characters in uppercase and lowercase, underscore, and numbers.");
    }
}