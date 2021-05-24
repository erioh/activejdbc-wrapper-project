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

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TimestampSetterBuilderStrategyTest {
    private final SetterBuilderStrategy setterBuilderStrategy = new TimestampSetterBuilderStrategy();

    @Test
    public void should_generate_setter() {
        // given
        String columnName = "COLUMN_NAME";
        String objectName = "object";
        String type = "Timestamp";
        String expectedGetter = String.format("public void setColumnName(Timestamp columnName) {%n" +
                "object.setTimestamp(\"COLUMN_NAME\", columnName);%n" +
                "}%n");

        // when
        String getterBody = setterBuilderStrategy.buildSetterBody(type, columnName, objectName);

        // then
        assertThat(getterBody).isEqualTo(expectedGetter);
    }

    @Test
    public void should_return_valid_classes() {
        // when | then
        assertThat(setterBuilderStrategy.typesToApply())
                .isEqualTo(Collections.singleton(Timestamp.class));
    }
}