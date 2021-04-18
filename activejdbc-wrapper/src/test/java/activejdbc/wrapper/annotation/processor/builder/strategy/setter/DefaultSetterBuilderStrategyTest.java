package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultSetterBuilderStrategyTest {
    private final SetterBuilderStrategy setterBuilderStrategy = new DefaultSetterBuilderStrategy();

    @Test
    public void should_generate_setter() {
        // given
        String columnName = "COLUMN_NAME";
        String objectName = "object";
        String type = "SomeType";
        String expectedGetter = String.format("public void setColumnName(%s columnName) {%n" +
                "object.set(\"COLUMN_NAME\", columnName);%n" +
                "}%n", type);

        // when
        String getterBody = setterBuilderStrategy.buildSetterBody(type, columnName, objectName);

        // then
        assertThat(getterBody).isEqualTo(expectedGetter);
    }

    @Test
    public void should_return_valid_classes() {
        // when | then
        assertThat(setterBuilderStrategy.typesToApply())
                .isEqualTo(Collections.emptySet());
    }
}