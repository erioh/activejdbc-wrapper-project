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

package activejdbc.wrapper.annotation;

import activejdbc.wrapper.ColumnConverter;

import java.lang.annotation.*;

/**
 * Declares one column of an ActiveJDBC model; the annotation processor generates a {@code <Model>Wrapper} with a
 * typed property for every declared column. Repeat it for each column.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Repeatable(value = ActiveJdbcRequiredProperties.class)
public @interface ActiveJdbcRequiredProperty {
    /**
     * The database column.
     *
     * @return the column name, as ActiveJDBC knows it, e.g. {@code FIRST_NAME}
     */
    String columnName();

    /**
     * The type of the generated property.
     *
     * @return the Java type of the property; not a primitive
     */
    Class<?> clazz();

    /**
     * The name of the generated property, for when the derived one doesn't fit.
     *
     * @return the property name; by default it's derived from the column name ({@code FIRST_NAME} → {@code firstName})
     */
    String desiredFieldName() default "";

    /**
     * Generates only the getter, for columns the application doesn't write: an ID, a column filled by the database.
     *
     * @return {@code true} to leave out the setter and the builder method
     */
    boolean readOnly() default false;

    /**
     * Leaves the property out of the generated {@code equals} and {@code hashCode}, e.g. an ID or a timestamp, so that
     * wrappers can be compared by their content.
     *
     * @return {@code true} to ignore the property in {@code equals} and {@code hashCode}
     */
    boolean excludeEqualsAndHashCode() default false;

    /**
     * Converts the property to the column's type and back, for types the wrapper doesn't support by itself; see
     * {@link activejdbc.wrapper.ColumnConverter}. Its first type argument must be {@link #clazz()}.
     *
     * @return the converter class; {@code ColumnConverter.class} (the default) means no converter
     */
    @SuppressWarnings("rawtypes")
    Class<? extends ColumnConverter> converter() default ColumnConverter.class;
}
