package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import org.junit.Test;

import java.time.LocalTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalTimeSetterBuilderStrategyTest {
    private final SetterBuilderStrategy setterBuilderStrategy = new LocalTimeSetterBuilderStrategy();

    @Test
    public void should_generate_setter() {
        // given
        String columnName = "COLUMN_NAME";
        String objectName = "object";
        String type = "LocalTime";
        String expectedGetter = String.format("public void setColumnName(LocalTime columnName) {%n" +
                "object.setTime(\"COLUMN_NAME\", java.sql.Time.valueOf(columnName));%n" +
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
                .isEqualTo(Collections.singleton(LocalTime.class));
    }

}