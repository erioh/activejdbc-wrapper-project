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

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DataProviderRunner.class)
public class DateSetterBuilderStrategyTest {
    private final SetterBuilderStrategy setterBuilderStrategy = new DateSetterBuilderStrategy();
    @DataProvider
    public static Object[][] getSetterTypes() {
        return new Object[][] {
                {"LocalDate"},
                {"Date"}
        };
    }
    @Test
    @UseDataProvider("getSetterTypes")
    public void should_generate_setter(String type) {
        // given
        String columnName = "COLUMN_NAME";
        String objectName = "object";
        String expectedGetter = String.format("public void setColumnName(%s columnName) {%n" +
                "object.setDate(\"COLUMN_NAME\", columnName);%n" +
                "}%n", type);

        // when
        String getterBody = setterBuilderStrategy.buildSetterBody(type, columnName, objectName);

        // then
        assertThat(getterBody).isEqualTo(expectedGetter);
    }

    @Test
    public void should_return_valid_classes() {
        // given
        Set<Class<?>> expected = new HashSet<>(Arrays.asList(LocalDate.class, Date.class));
        // when | then
        assertThat(setterBuilderStrategy.typesToApply())
                .isEqualTo(expected);
    }
}