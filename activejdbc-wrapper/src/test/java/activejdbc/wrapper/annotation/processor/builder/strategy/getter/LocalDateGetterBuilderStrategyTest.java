package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateGetterBuilderStrategyTest {
    private final GetterBuilderStrategy getterBuilderStrategy = new LocalDateGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        String columnName = "COLUMN_NAME";
        String type = "LocalDate";
        String objectName = "object";
        String expectedGetter = String.format("public LocalDate getColumnName() {%n" +
                "return java.util.Optional.ofNullable(object.getDate(\"COLUMN_NAME\"))%n" +
                ".map(java.sql.Date::toLocalDate)%n" +
                ".orElse(null);%n" +
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
                .isEqualTo(Collections.singleton(LocalDate.class));
    }
}