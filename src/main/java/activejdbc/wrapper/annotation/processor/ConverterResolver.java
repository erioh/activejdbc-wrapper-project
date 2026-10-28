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

import activejdbc.wrapper.annotation.processor.context.ColumnContext;
import activejdbc.wrapper.annotation.processor.util.AnnotationValueExtractor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Reads the {@code converter} of a column: checks that the wrapper can create and use it and finds the types it
 * converts between.
 */
class ConverterResolver {
    static final String CONVERTER_INTERFACE = "activejdbc.wrapper.ColumnConverter";

    private final ProcessingEnvironment processingEnv;

    ConverterResolver(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    /**
     * @return the column with its converter, the column itself if it has none, or null if the converter can't be
     * used (the error is already reported on the model)
     */
    ColumnContext resolve(AnnotationMirror annotationMirror, ColumnContext column, TypeElement model) {
        Optional<TypeMirror> converterValue = typeValue(annotationMirror, "converter");
        if (!converterValue.isPresent() || converterValue.get().getKind() != TypeKind.DECLARED) {
            return column;
        }
        DeclaredType converterType = (DeclaredType) converterValue.get();
        TypeElement converter = (TypeElement) converterType.asElement();
        String converterName = converter.getQualifiedName().toString();
        if (CONVERTER_INTERFACE.equals(converterName)) {
            return column;
        }
        String columnName = column.getColumnName();
        if (!isCreatable(converter, columnName, model)) {
            return null;
        }
        List<? extends TypeMirror> typeArguments = converterTypeArguments(converterType);
        if (typeArguments.size() != 2 || !typeArguments.stream().allMatch(ConverterResolver::isConcrete)) {
            error(String.format("Converter [%s] of column [%s] must implement ColumnConverter with concrete type arguments, e.g. ColumnConverter<Money, BigDecimal>.",
                    converterName, columnName), model);
            return null;
        }
        TypeMirror propertyType = typeArguments.get(0);
        TypeMirror databaseType = typeArguments.get(1);
        Types types = processingEnv.getTypeUtils();
        Optional<TypeMirror> clazz = typeValue(annotationMirror, "clazz");
        if (clazz.isPresent() && !types.isSameType(types.erasure(propertyType), types.erasure(clazz.get()))) {
            error(String.format("Converter [%s] of column [%s] converts [%s], but the column's clazz is [%s].",
                    converterName, columnName, propertyType, clazz.get()), model);
            return null;
        }
        boolean databaseEnum = databaseType.getKind() == TypeKind.DECLARED
                && ((DeclaredType) databaseType).asElement().getKind() == ElementKind.ENUM;
        return column.withConverter(propertyType.toString(),
                new ColumnContext.Converter(converterName, databaseType.toString(), databaseEnum));
    }

    /**
     * The wrapper is generated next to the model and creates the converter with new Converter().
     */
    private boolean isCreatable(TypeElement converter, String columnName, TypeElement model) {
        String converterName = converter.getQualifiedName().toString();
        if (converter.getKind() != ElementKind.CLASS || converter.getModifiers().contains(Modifier.ABSTRACT)) {
            error(String.format("Converter [%s] of column [%s] must be a class that isn't abstract: the wrapper creates its instance.",
                    converterName, columnName), model);
            return false;
        }
        if (!converter.getTypeParameters().isEmpty()) {
            error(String.format("Converter [%s] of column [%s] must not have type parameters.", converterName, columnName), model);
            return false;
        }
        PackageElement modelPackage = processingEnv.getElementUtils().getPackageOf(model);
        boolean samePackage = processingEnv.getElementUtils().getPackageOf(converter).equals(modelPackage);
        for (Element current = converter; current.getKind() != ElementKind.PACKAGE; current = current.getEnclosingElement()) {
            Set<Modifier> modifiers = current.getModifiers();
            boolean accessible = samePackage ? !modifiers.contains(Modifier.PRIVATE) : modifiers.contains(Modifier.PUBLIC);
            boolean nested = current.getEnclosingElement().getKind() != ElementKind.PACKAGE;
            if (!accessible || nested && !modifiers.contains(Modifier.STATIC)) {
                error(String.format("Converter [%s] of column [%s] must be accessible from the wrapper in package [%s]: a public class, or a static nested class of one.",
                        converterName, columnName, modelPackage.getQualifiedName()), model);
                return false;
            }
        }
        boolean hasConstructor = ElementFilter.constructorsIn(converter.getEnclosedElements()).stream()
                .filter(constructor -> constructor.getParameters().isEmpty())
                .anyMatch(constructor -> samePackage
                        ? !constructor.getModifiers().contains(Modifier.PRIVATE)
                        : constructor.getModifiers().contains(Modifier.PUBLIC));
        if (!hasConstructor) {
            error(String.format("Converter [%s] of column [%s] needs a public constructor without parameters: the wrapper creates its instance.",
                    converterName, columnName), model);
            return false;
        }
        return true;
    }

    /**
     * Type arguments of ColumnConverter as the converter implements it, found through its supertypes.
     */
    private List<? extends TypeMirror> converterTypeArguments(DeclaredType converterType) {
        Types types = processingEnv.getTypeUtils();
        Deque<TypeMirror> toVisit = new ArrayDeque<>(types.directSupertypes(converterType));
        while (!toVisit.isEmpty()) {
            TypeMirror supertype = toVisit.poll();
            if (supertype.getKind() != TypeKind.DECLARED) {
                continue;
            }
            DeclaredType declared = (DeclaredType) supertype;
            if (((TypeElement) declared.asElement()).getQualifiedName().contentEquals(CONVERTER_INTERFACE)) {
                return declared.getTypeArguments();
            }
            toVisit.addAll(types.directSupertypes(supertype));
        }
        return Collections.emptyList();
    }

    private static boolean isConcrete(TypeMirror type) {
        return type.getKind() == TypeKind.DECLARED || type.getKind() == TypeKind.ARRAY;
    }

    private Optional<TypeMirror> typeValue(AnnotationMirror annotationMirror, String name) {
        return AnnotationValueExtractor.extract(annotationMirror.getElementValues(), name)
                .map(AnnotationValue::getValue)
                .filter(TypeMirror.class::isInstance)
                .map(TypeMirror.class::cast);
    }

    private void error(String message, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
