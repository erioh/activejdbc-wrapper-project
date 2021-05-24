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

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

public class AnnotationValueExtractor {
    public static AnnotationValue extract(Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues, String expectedPropertyName) {

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
            String propertyName = entry.getKey().getSimpleName().toString();
            if (propertyName.equalsIgnoreCase(expectedPropertyName)) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException(String.format("Unknown property [%s]", expectedPropertyName));
    }
}
