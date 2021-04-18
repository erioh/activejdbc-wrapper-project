package activejdbc.wrapper.annotation.processor.builder.strategy.getter;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class StringGetterBuilderStrategyTest {
    private final GetterBuilderStrategy getterBuilderStrategy = new StringGetterBuilderStrategy();

    @Test
    public void should_generate_getter() {
        // given
        String columnName = "COLUMN_NAME";
        String type = "String";
        String objectName = "object";
        String expectedGetter = String.format("public String getColumnName() {%n" +
                "return object.getString(\"COLUMN_NAME\");%n" +
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
                .isEqualTo(Collections.singleton(String.class));
    }
}