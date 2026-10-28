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
package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter.EnumSetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter.SetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumSetterBuilderStrategyTest {

    private final SetterBuilderStrategy setterBuilderStrategy = new EnumSetterBuilderStrategy();

    @Test
    public void should_generate_setter() {
        // given
        ColumnContext columnContext = new ColumnContext("com.example.Color", "COLOR", "paint", true);
        String expectedSetter = String.format("public void setPaint(com.example.Color paint) {%n" +
                "this.object.set(\"COLOR\", paint == null ? null : paint.name());%n" +
                "}%n");

        // when
        String setterBody = setterBuilderStrategy.buildSetterBody(columnContext, "object");

        // then
        assertThat(setterBody).isEqualTo(expectedSetter);
    }

    @Test
    public void should_not_claim_any_type() {
        // when | then
        assertThat(setterBuilderStrategy.typesToApply()).isEmpty();
    }
}
