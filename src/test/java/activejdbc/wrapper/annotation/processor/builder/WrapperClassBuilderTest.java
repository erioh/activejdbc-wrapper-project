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

package activejdbc.wrapper.annotation.processor.builder;

import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import activejdbc.wrapper.annotation.processor.test.util.ContentExtractor;
import activejdbc.wrapper.annotation.processor.wrapper.builder.WrapperClassBuilder;
import activejdbc.wrapper.annotation.processor.wrapper.builder.strategy.GetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.wrapper.builder.strategy.SetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.wrapper.builder.strategy.StrategyHolder;
import activejdbc.wrapper.annotation.processor.wrapper.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.wrapper.builder.strategy.setter.SetterBuilderStrategy;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(DataProviderRunner.class)
public class WrapperClassBuilderTest {

    @Test
    public void should_create_body_with_only_default_implementation() throws IOException, URISyntaxException {

        // given
        String packageName = "package.name";
        String activejdbcObjectClassName = "ObjectClassName";
        AnnotationProcessorContext annotationProcessorContext = mock(AnnotationProcessorContext.class);
        given(annotationProcessorContext.getWrapperSuffix()).willReturn("Wrapper");
        String expectedBody = ContentExtractor.fromFile("expected_empty_class_body.txt");
        List<ColumnContext> columnContexts = new ArrayList<>();
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, columnContexts, annotationProcessorContext);

        // when
        String classBody = wrapperClassBuilder.buildClassBody();
        //then
        assertThat(classBody).isEqualTo(expectedBody);
    }

    @DataProvider
    public static Object[][] desired_field_name() {
        return new Object[][]{
                {""},
                {"customName"}
        };
    }

    @Test
    @UseDataProvider("desired_field_name")
    public void should_create_body_with_setter(String desiredFieldName) throws IOException, URISyntaxException {

        // given
        String packageName = "package.name";
        String activejdbcObjectClassName = "ObjectClassName";
        String activejdbcObjectName = "objectClassName";
        AnnotationProcessorContext annotationProcessorContext = mock(AnnotationProcessorContext.class);
        given(annotationProcessorContext.getWrapperSuffix()).willReturn("Wrapper");
        String expectedBody = ContentExtractor.fromFile("expected_class_body_with_setter.txt");
        StrategyHolder<SetterBuilderStrategy> setterStrategyHolder = mock(SetterBuilderStrategyHolder.class);
        SetterBuilderStrategy stringStrategy = mock(SetterBuilderStrategy.class);
        ColumnContext columnContext = new ColumnContext("String", "STRING_COLUMN", desiredFieldName);
        given(stringStrategy.buildSetterBody(columnContext, activejdbcObjectName))
                .willReturn(String.format("setter() {}%n"));
        given(setterStrategyHolder.getStrategy("String")).willReturn(stringStrategy);
        given(annotationProcessorContext.getSetterBuilderStrategyHolder()).willReturn(setterStrategyHolder);
        List<ColumnContext> columnContexts = new ArrayList<>();
        columnContexts.add(columnContext);

        // when
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, columnContexts, annotationProcessorContext);
        wrapperClassBuilder.withSetters();
        String classBody = wrapperClassBuilder.buildClassBody();
        // then
        assertThat(classBody).isEqualTo(expectedBody);
    }

    @DataProvider
    public static Object[][] desired_field_name_for_equals_and_hashcode() {
        return new Object[][]{
                {"", "expected_class_body_with_getter.txt"},
                {"customName", "expected_class_body_with_getter_with_custom_fields.txt"}
        };
    }


    @Test
    @UseDataProvider("desired_field_name_for_equals_and_hashcode")
    public void should_create_body_with_getter_plus_equals_and_hash_code_and_toString(String desiredFieldName, String file) throws IOException, URISyntaxException {

        // given
        String packageName = "package.name";
        String activejdbcObjectClassName = "ObjectClassName";
        String activejdbcObjectName = "objectClassName";
        AnnotationProcessorContext annotationProcessorContext = mock(AnnotationProcessorContext.class);
        given(annotationProcessorContext.getWrapperSuffix()).willReturn("Wrapper");
        String expectedBody = ContentExtractor.fromFile(file);
        StrategyHolder<GetterBuilderStrategy> getterStrategyHolder = mock(GetterBuilderStrategyHolder.class);
        given(annotationProcessorContext.getGetterBuilderStrategyHolder()).willReturn(getterStrategyHolder);
        GetterBuilderStrategy getterStrategy = mock(GetterBuilderStrategy.class);
        given(getterStrategyHolder.getStrategy("String"))
                .willReturn(getterStrategy);
        ColumnContext columnContext = new ColumnContext("String", "STRING_COLUMN", desiredFieldName);
        given(getterStrategy.buildGetterBody(columnContext, activejdbcObjectName))
                .willReturn(String.format("getter() {}%n"));
        List<ColumnContext> columnContexts = new ArrayList<>();
        // when
        columnContexts.add(columnContext);
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, columnContexts, annotationProcessorContext);
        wrapperClassBuilder.withGetters();
        wrapperClassBuilder.withEquals();
        wrapperClassBuilder.withHashCode();
        wrapperClassBuilder.withToString();
        String classBody = wrapperClassBuilder.buildClassBody();
        // then
        assertThat(classBody).isEqualTo(expectedBody);
    }

    @Test
    public void should_create_body_with_get_activejdbc_object_method() throws IOException, URISyntaxException {

        // given
        String packageName = "package.name";
        String activejdbcObjectClassName = "ObjectClassName";
        AnnotationProcessorContext annotationProcessorContext = mock(AnnotationProcessorContext.class);
        given(annotationProcessorContext.getWrapperSuffix()).willReturn("Wrapper");
        String expectedBody = ContentExtractor.fromFile("expected_class_body_with_get_activejdbc_object_method.txt");
        List<ColumnContext> columnContexts = new ArrayList<>();
        // when
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, columnContexts, annotationProcessorContext);
        wrapperClassBuilder.withMethodGetActivejdbcObject();
        String classBody = wrapperClassBuilder.buildClassBody();
        // then
        assertThat(classBody).isEqualTo(expectedBody);
    }

    @Test
    public void should_create_body_with_builder() throws IOException, URISyntaxException {
        // given
        String packageName = "package.name";
        String activejdbcObjectClassName = "ObjectClassName";
        AnnotationProcessorContext annotationProcessorContext = mock(AnnotationProcessorContext.class);
        given(annotationProcessorContext.getWrapperSuffix()).willReturn("Wrapper");
        String expectedBody = ContentExtractor.fromFile("expected_class_body_with_builder.txt");
        List<ColumnContext> columnContexts = new ArrayList<>();
        // when
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, columnContexts, annotationProcessorContext);
        wrapperClassBuilder.withBuilder();
        String classBody = wrapperClassBuilder.buildClassBody();
        // then
        assertThat(classBody).isEqualTo(expectedBody);
    }


}