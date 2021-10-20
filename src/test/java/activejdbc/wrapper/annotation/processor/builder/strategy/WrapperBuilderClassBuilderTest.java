package activejdbc.wrapper.annotation.processor.builder.strategy;

import activejdbc.wrapper.annotation.processor.test.util.ContentExtractor;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WrapperBuilderClassBuilderTest {

    @Test
    public void should_create_builder_class_body() throws IOException, URISyntaxException {
        // given
        String wrapperClassName = "WrapperClassName";
        String builderClassName = "WrapperClassNameBuilder";
        Map<String, String> columnNameWithType = new HashMap<>();
        columnNameWithType.put("COLUMN_NAME", "java.lang.ClassName");
        // when
        WrapperBuilderClassBuilder wrapperBuilderClassBuilder = new WrapperBuilderClassBuilder(wrapperClassName, builderClassName, columnNameWithType);
        String result = wrapperBuilderClassBuilder.buildClassBody();
        // then
        String expectedBody = ContentExtractor.fromFile("expected_builder_class_body.txt");
        assertThat(result).isEqualTo(expectedBody);
    }

}