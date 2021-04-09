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
