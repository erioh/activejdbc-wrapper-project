package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateTimeGetterBuilderStrategyTest {
    private final GetterBuilderStrategy getterBuilderStrategy = new LocalDateTimeGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        String columnName = "COLUMN_NAME";
        String type = "LocalDateTime";
        String objectName = "object";
        String expectedGetter = String.format("public LocalDateTime getColumnName() {%n" +
                "return java.util.Optional.ofNullable(object.getTimestamp(\"COLUMN_NAME\"))%n" +
                ".map(java.sql.Timestamp::toLocalDateTime)%n" +
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
                .isEqualTo(Collections.singleton(LocalDateTime.class));
    }
}