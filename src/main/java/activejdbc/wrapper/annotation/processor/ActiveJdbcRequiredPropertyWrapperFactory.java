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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ActiveJdbcRequiredPropertyWrapperFactory {
    private final AnnotationProcessorContext annotationProcessorContext;

    public ActiveJdbcRequiredPropertyWrapperFactory(AnnotationProcessorContext annotationProcessorContext) {
        this.annotationProcessorContext = annotationProcessorContext;
    }

    public String build(String packageName, String className, List<? extends AnnotationMirror> annotationMirrors) {
        List<ColumnContext> columnContexts = annotationMirrors.stream()
                .map(this::getColumnContext)
                .collect(Collectors.toList());
        WrapperClassBuilder wrapperClassBuilder = new WrapperClassBuilder(packageName, className, columnContexts, annotationProcessorContext);
        // add setters
        return wrapperClassBuilder.withSetters()
                // add getters
                .withGetters()
                // add get activejdbc object method
                .withMethodGetActivejdbcObject()
                // add toString (using getters)
                .withToString()
                // add equals
                .withEquals()
                // and hashcode (using getters)
                .withHashCode()
                // add builder method with builder class
                .withBuilder()
                .buildClassBody();
    }

    private ColumnContext getColumnContext(AnnotationMirror annotationMirror) {
        String clazz = extract(annotationMirror, "clazz");
        String columnName = extract(annotationMirror, "columnName");
        String desiredFieldName = extract(annotationMirror, "desiredFieldName");
        return new ColumnContext(clazz, columnName, desiredFieldName);
    }

    private String extract(AnnotationMirror annotationMirror, String property) {
        return AnnotationValueExtractor.extract(annotationMirror.getElementValues(), property)
                .map(AnnotationValue::getValue)
                .map(Objects::toString)
                .orElse("");
    }
}
