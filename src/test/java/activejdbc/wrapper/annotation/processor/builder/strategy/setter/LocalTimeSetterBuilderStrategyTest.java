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

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter.LocalTimeSetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.setter.SetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DataProviderRunner.class)
public class LocalTimeSetterBuilderStrategyTest {
    private final SetterBuilderStrategy setterBuilderStrategy = new LocalTimeSetterBuilderStrategy();

    @DataProvider
    public static Object[][] desired_field_name() {
        return new Object[][]{
                {"", "setColumnName", "columnName"},
                {"customFieldName", "setCustomFieldName", "customFieldName"},
        };
    }

    @Test
    @UseDataProvider("desired_field_name")
    public void should_generate_setter(String desiredFieldName, String methodName, String expectedColumnName) {
        // given
        String columnName = "COLUMN_NAME";
        String objectName = "object";
        String type = "LocalTime";
        String expectedGetter = String.format("public void %s(LocalTime %s) {%n" +
                "object.setTime(\"COLUMN_NAME\", java.util.Optional.ofNullable(%s)%n" +
                ".map(java.sql.Time::valueOf).orElse(null));%n" +
                "}%n", methodName, expectedColumnName, expectedColumnName);
        ColumnContext columnContext = new ColumnContext(type, columnName, desiredFieldName);

        // when
        String getterBody = setterBuilderStrategy.buildSetterBody(columnContext, objectName);

        // then
        assertThat(getterBody).isEqualTo(expectedGetter);
    }

    @Test
    public void should_return_valid_classes() {
        // when | then
        assertThat(setterBuilderStrategy.typesToApply())
                .isEqualTo(Collections.singleton(LocalTime.class));
    }

}