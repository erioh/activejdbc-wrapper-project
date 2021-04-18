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