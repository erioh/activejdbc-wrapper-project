package activejdbc.wrapper.annotation.processor.builder;

import org.javalite.activejdbc.Model;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;


public class ActivejdbcWrapperManipulatorTest {
    private final ActivejdbcWrapperManipulator manipulator = new ActivejdbcWrapperManipulator();

    @Test
    public void should_extract_actual_object() {
        // given
        WrappedClass wrappedClass = new WrappedClass();
        // when
        Model activejdbcObject = manipulator.getActivejdbcObject(wrappedClass);
        // then
        assertThat(activejdbcObject).isNotNull();
    }

    public static class WrappedClass extends ActivejdbcWrapper<Model> {
        private Model model = Mockito.mock(Model.class);

        @Override
        protected Model getActivejdbcObject() {
            return model;
        }
    }
}