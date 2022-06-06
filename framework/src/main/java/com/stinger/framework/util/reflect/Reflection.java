package com.stinger.framework.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Reflection {

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
}
