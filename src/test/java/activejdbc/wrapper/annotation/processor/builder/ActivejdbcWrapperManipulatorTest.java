/*
Copyright 2021-2021 Serhii Demenkov
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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