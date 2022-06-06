package com.stinger.framework.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Reflection {

    public static void setField(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static Set<? extends Field> getFieldsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotation) {
        Set<? extends Field> fields = getFields(cls);
        return fields.stream()
                .filter((f)-> f.getAnnotation(annotation) != null)
                .collect(Collectors.toSet());
    }

    public static Set<? extends Field> getFields(Class<?> cls) {
        Set<Field> fields = new HashSet<>();
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        fields.addAll(Arrays.asList(cls.getFields()));

        return fields;
    }

    public static <E extends Enum<?>> E[] getEnumValues(Class<E> enumClass)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = enumClass.getDeclaredField("$VALUES");
        field.setAccessible(true);

        Object values = field.get(null);
        //noinspection unchecked
        return (E[]) values;
    }
}
