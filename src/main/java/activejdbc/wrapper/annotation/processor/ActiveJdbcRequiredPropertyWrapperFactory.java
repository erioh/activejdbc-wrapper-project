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

package activejdbc.wrapper.annotation.processor;

import activejdbc.wrapper.annotation.processor.builder.WrapperClassBuilder;
import activejdbc.wrapper.annotation.processor.context.AnnotationProcessorContext;
import activejdbc.wrapper.annotation.processor.util.AnnotationValueExtractor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ActiveJdbcRequiredPropertyWrapperFactory {
    private final AnnotationProcessorContext annotationProcessorContext;

    public ActiveJdbcRequiredPropertyWrapperFactory(AnnotationProcessorContext annotationProcessorContext) {
        this.annotationProcessorContext = annotationProcessorContext;
    }

    public String build(String packageName, String className, List<? extends AnnotationMirror> annotationMirrors) {
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, className, annotationProcessorContext);
        // add setters
        addMethod(annotationMirrors, wrapperClassBuilder::withSetter);
        // add getters
        addMethod(annotationMirrors, wrapperClassBuilder::withGetter);
        // add get activejdbc object method
        wrapperClassBuilder.withMethodGetActivejdbcObject();
        // add toString (using getters)
        wrapperClassBuilder.withToString();
        // add equals
        wrapperClassBuilder.withEquals();
        // and hashcode (using getters)
        wrapperClassBuilder.withHashCode();
        // add builder method with builder class
        wrapperClassBuilder.withBuilder(extractColumnNameAndType(annotationMirrors));
        return wrapperClassBuilder.buildClassBody();
    }

    private void addMethod(List<? extends AnnotationMirror> annotationMirrors, BiConsumer<String, String> classBuilderConsumer) {
        annotationMirrors.forEach(annotationMirror -> {
            AnnotationValue clazz = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz");
            AnnotationValue field = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "field");
            String columnName = field.getValue().toString();
            classBuilderConsumer.accept(clazz.getValue().toString(), columnName);
        });
    }

    private Map<String, String> extractColumnNameAndType(List<? extends AnnotationMirror> annotationMirrors) {
        Map<String, String> columnNameAndType = new HashMap<>();
        annotationMirrors.forEach(annotationMirror -> {
            AnnotationValue clazz = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "clazz");
            AnnotationValue field = AnnotationValueExtractor.extract(annotationMirror.getElementValues(), "field");
            String columnName = field.getValue().toString();
            String className = clazz.getValue().toString();
            columnNameAndType.put(columnName, className);
        });
        return columnNameAndType;
    }
}
