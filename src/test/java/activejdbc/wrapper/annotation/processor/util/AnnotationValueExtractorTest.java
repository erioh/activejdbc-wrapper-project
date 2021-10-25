/*
Copyright 2021-2021 Serhii Demenkov
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        Optional<AnnotationValue> actualAnnotationValue = AnnotationValueExtractor.extract(map, expectedPropertyName);
        // then
        assertThat(actualAnnotationValue).isEqualTo(Optional.of(annotationValue));
    }

    @Test
    public void should_return_empty_optional_if_property_is_not_set() {
        // given
        Map<ExecutableElement, AnnotationValue> map = new HashMap<>();
        map.put(executableElement, annotationValue);
        given(executableElement.getSimpleName()).willReturn(name);
        given(name.toString()).willReturn("actualPropertyName");
        // when | then
        assertThat(AnnotationValueExtractor.extract(map, "expectedPropertyName"))
                .isEmpty();
    }

}