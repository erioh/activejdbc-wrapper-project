package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import org.junit.Test;

import java.sql.Time;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeGetterBuilderStrategyTest {
    private final GetterBuilderStrategy getterBuilderStrategy = new TimeGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        String columnName = "COLUMN_NAME";
        String type = "Time";
        String objectName = "object";
        String expectedGetter = String.format("public Time getColumnName() {%n" +
                "return new java.sql.Time(object.getDate(\"COLUMN_NAME\").getTime());%n" +
                "}%n");

        // when
        String getterBody = getterBuilderStrategy.buildGetterBody(type, columnName, objectName);

        // then
        assertThat(getterBody).isEqualTo(expectedGetter);
    }

    @Test
    public void should_return_valid_classes() {
        // when | then
        assertThat(getterBuilderStrategy.typesToApply())
                .isEqualTo(Collections.singleton(Time.class));
    }
}