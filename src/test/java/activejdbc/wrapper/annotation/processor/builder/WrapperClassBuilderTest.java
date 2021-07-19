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

import activejdbc.wrapper.annotation.processor.builder.strategy.GetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.builder.strategy.SetterBuilderStrategyHolder;
import activejdbc.wrapper.annotation.processor.builder.strategy.StrategyHolder;
import activejdbc.wrapper.annotation.processor.builder.strategy.getter.GetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.builder.strategy.setter.SetterBuilderStrategy;
import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.test.util.ContentExtractor;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class WrapperClassBuilderTest {

    @Test
    public void should_create_body_with_only_default_implementation() throws IOException, URISyntaxException {

        // given
        String packageName = "package.name";
        String activejdbcObjectClassName = "ObjectClassName";
        AnnotationProcessorContext annotationProcessorContext = mock(AnnotationProcessorContext.class);
        given(annotationProcessorContext.getWrapperSuffix()).willReturn("Wrapper");
        String expectedBody = ContentExtractor.fromFile("expected_empty_class_body.txt");
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, annotationProcessorContext);

        // when
        String classBody = wrapperClassBuilder.buildClassBody();
        //then
        assertThat(classBody).isEqualTo(expectedBody);
    }

    @Test
    public void should_create_body_with_setter() throws IOException, URISyntaxException {

        // given
        String packageName = "package.name";
        String activejdbcObjectClassName = "ObjectClassName";
        String activejdbcObjectName = "objectClassName";
        AnnotationProcessorContext annotationProcessorContext = mock(AnnotationProcessorContext.class);
        given(annotationProcessorContext.getWrapperSuffix()).willReturn("Wrapper");
        String expectedBody = ContentExtractor.fromFile("expected_class_body_with_setter.txt");
        StrategyHolder<SetterBuilderStrategy> setterStrategyHolder = mock(SetterBuilderStrategyHolder.class);
        SetterBuilderStrategy stringStrategy = mock(SetterBuilderStrategy.class);
        given(stringStrategy.buildSetterBody("String", "STRING_COLUMN", activejdbcObjectName))
                .willReturn(String.format("setter() {}%n"));
        given(setterStrategyHolder.getStrategy("String")).willReturn(stringStrategy);
        given(annotationProcessorContext.getSetterBuilderStrategyHolder()).willReturn(setterStrategyHolder);

        // when
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, annotationProcessorContext);
        wrapperClassBuilder.withSetter("String", "STRING_COLUMN");
        String classBody = wrapperClassBuilder.buildClassBody();
        // then
        assertThat(classBody).isEqualTo(expectedBody);
    }

    @Test
    public void should_create_body_with_getter_plus_equals_and_hash_code_and_toString() throws IOException, URISyntaxException {

        // given
        String packageName = "package.name";
        String activejdbcObjectClassName = "ObjectClassName";
        String activejdbcObjectName = "objectClassName";
        AnnotationProcessorContext annotationProcessorContext = mock(AnnotationProcessorContext.class);
        given(annotationProcessorContext.getWrapperSuffix()).willReturn("Wrapper");
        String expectedBody = ContentExtractor.fromFile("expected_class_body_with_getter.txt");
        StrategyHolder<GetterBuilderStrategy> getterStrategyHolder = mock(GetterBuilderStrategyHolder.class);
        given(annotationProcessorContext.getGetterBuilderStrategyHolder()).willReturn(getterStrategyHolder);
        GetterBuilderStrategy getterStrategy = mock(GetterBuilderStrategy.class);
        given(getterStrategyHolder.getStrategy("String"))
                .willReturn(getterStrategy);
        given(getterStrategy.buildGetterBody("String", "STRING_COLUMN", activejdbcObjectName))
                .willReturn(String.format("getter() {}%n"));
        // when
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, annotationProcessorContext);
        wrapperClassBuilder.withGetter("String", "STRING_COLUMN");
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
        // when
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, activejdbcObjectClassName, annotationProcessorContext);
        wrapperClassBuilder.withMethodGetActivejdbcObject();
        String classBody = wrapperClassBuilder.buildClassBody();
        // then
        assertThat(classBody).isEqualTo(expectedBody);
    }


}