package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import org.junit.Test;

import java.sql.Date;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class DateGetterBuilderStrategyTest {
    private final GetterBuilderStrategy getterBuilderStrategy = new DateGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        String columnName = "COLUMN_NAME";
        String type = "Date";
        String objectName = "object";
        String expectedGetter = String.format("public Date getColumnName() {%n" +
                "return object.getDate(\"COLUMN_NAME\");%n" +
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
                .isEqualTo(Collections.singleton(Date.class));
    }
}