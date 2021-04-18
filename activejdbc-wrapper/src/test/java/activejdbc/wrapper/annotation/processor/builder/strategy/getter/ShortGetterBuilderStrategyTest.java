package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ShortGetterBuilderStrategyTest {
    private final GetterBuilderStrategy getterBuilderStrategy = new ShortGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        String columnName = "COLUMN_NAME";
        String type = "Short";
        String objectName = "object";
        String expectedGetter = String.format("public Short getColumnName() {%n" +
                "return object.getShort(\"COLUMN_NAME\");%n" +
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
                .isEqualTo(Collections.singleton(Short.class));
    }
}