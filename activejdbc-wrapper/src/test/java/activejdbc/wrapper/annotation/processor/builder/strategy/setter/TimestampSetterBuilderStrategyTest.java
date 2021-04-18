package activejdbc.wrapper.annotation.processor.builder.strategy.setter;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TimestampSetterBuilderStrategyTest {
    private final SetterBuilderStrategy setterBuilderStrategy = new TimestampSetterBuilderStrategy();

    @Test
    public void should_generate_setter() {
        // given
        String columnName = "COLUMN_NAME";
        String objectName = "object";
        String type = "Timestamp";
        String expectedGetter = String.format("public void setColumnName(Timestamp columnName) {%n" +
                "object.setTimestamp(\"COLUMN_NAME\", columnName);%n" +
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
                .isEqualTo(Collections.singleton(Timestamp.class));
    }
}