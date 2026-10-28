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
package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.EnumGetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumGetterBuilderStrategyTest {

    private final GetterBuilderStrategy getterBuilderStrategy = new EnumGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        ColumnContext columnContext = new ColumnContext("com.example.Color", "COLOR", "", true);
        String expectedGetter = String.format("public com.example.Color getColor() {%n" +
                "return java.util.Optional.ofNullable(object.getString(\"COLOR\"))%n" +
                ".map(String::trim)%n" +
                ".map(com.example.Color::valueOf)%n" +
                ".orElse(null);%n" +
                "}%n");

        // when
        String getterBody = getterBuilderStrategy.buildGetterBody(columnContext, "object");

        // then
        assertThat(getterBody).isEqualTo(expectedGetter);
    }

    @Test
    public void should_not_claim_any_type() {
        // when | then
        assertThat(getterBuilderStrategy.typesToApply()).isEmpty();
    }
}
