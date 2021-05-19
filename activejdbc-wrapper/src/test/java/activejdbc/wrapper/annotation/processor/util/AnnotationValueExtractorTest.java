package activejdbc.wrapper.annotation.processor.util;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@RunWith(DataProviderRunner.class)
public class AnnotationValueExtractorTest {
    @Mock
    private ExecutableElement executableElement;
    @Mock
    private Name name;
    @Mock
    private AnnotationValue annotationValue;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DataProvider
    public static Object[][] parameterName() {
        return new Object[][]{
                {"param"},
                {"PARAM"},
        };
    }

    @Test
    @UseDataProvider("parameterName")
    public void should_extract_parameter(String expectedPropertyName) {
        // given
        Map<ExecutableElement, AnnotationValue> map = new HashMap<>();
        map.put(executableElement, annotationValue);
        given(executableElement.getSimpleName()).willReturn(name);
        given(name.toString()).willReturn(expectedPropertyName.toLowerCase());
        // when
        AnnotationValue actualAnnotationValue = AnnotationValueExtractor.extract(map, expectedPropertyName);
        // then
        assertThat(actualAnnotationValue).isEqualTo(annotationValue);
    }

    @Test
    public void should_throw_IllegalArgumentException() {
        // given
        Map<ExecutableElement, AnnotationValue> map = new HashMap<>();
        map.put(executableElement, annotationValue);
        given(executableElement.getSimpleName()).willReturn(name);
        given(name.toString()).willReturn("actualPropertyName");
        // when | then
        assertThatThrownBy(() -> AnnotationValueExtractor.extract(map, "expectedPropertyName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown property [expectedPropertyName]");
    }

}