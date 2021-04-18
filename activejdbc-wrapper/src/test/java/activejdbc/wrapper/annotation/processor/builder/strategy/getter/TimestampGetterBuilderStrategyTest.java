package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TimestampGetterBuilderStrategyTest {
    private final GetterBuilderStrategy getterBuilderStrategy = new TimestampGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        String columnName = "COLUMN_NAME";
        String type = "Timestamp";
        String objectName = "object";
        String expectedGetter = String.format("public Timestamp getColumnName() {%n" +
                "return object.getTimestamp(\"COLUMN_NAME\");%n" +
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
                .isEqualTo(Collections.singleton(Timestamp.class));
    }
}