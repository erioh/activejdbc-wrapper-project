package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import org.junit.Test;

import java.time.LocalTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalTimeGetterBuilderStrategyTest {
    private final GetterBuilderStrategy getterBuilderStrategy = new LocalTimeGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        String columnName = "COLUMN_NAME";
        String type = "LocalTime";
        String objectName = "object";
        String expectedGetter = String.format("public LocalTime getColumnName() {%n" +
                "return java.util.Optional.ofNullable(object.getTime(\"COLUMN_NAME\"))%n" +
                ".map(java.sql.Time::toLocalTime)%n" +
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
                .isEqualTo(Collections.singleton(LocalTime.class));
    }

}