package activejdbc.wrapper.annotation.processor.builder.strategy;

import activejdbc.wrapper.annotation.processor.adapter.builder.strategy.WrapperBuilderClassBuilder;
import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import activejdbc.wrapper.annotation.processor.test.util.ContentExtractor;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DataProviderRunner.class)
public class WrapperBuilderClassBuilderTest {

    @DataProvider
    public static Object[][] desired_field_name() {
        return new Object[][]{
                {"", "expected_builder_class_body.txt"},
                {"customFieldName", "expected_builder_class_body_with_custom_fields.txt"},
        };
    }

    @Test
    @UseDataProvider("desired_field_name")
    public void should_create_builder_class_body(String desiredFieldName, String file) throws IOException, URISyntaxException {
        // given
        String wrapperClassName = "WrapperClassName";
        ColumnContext columnContext = new ColumnContext("java.lang.ClassName", "COLUMN_NAME", desiredFieldName);
        List<ColumnContext> columnContexts = new ArrayList<>();
        columnContexts.add(columnContext);
        // when
        AnnotationProcessorContext annotationProcessorContext = new AnnotationProcessorContext("", "with");
        WrapperBuilderClassBuilder wrapperBuilderClassBuilder = new WrapperBuilderClassBuilder(wrapperClassName, annotationProcessorContext, columnContexts);
        String result = wrapperBuilderClassBuilder.buildClassBody();
        // then
        String expectedBody = ContentExtractor.fromFile(file);
        assertThat(result).isEqualTo(expectedBody);
    }

}