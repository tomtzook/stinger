package com.stinger.framework.db.hibernate;

import com.stinger.framework.util.reflect.Reflection;
import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Set;

public class JpaHelper {

    public static <T> String convertColumnName(Class<T> type, String name) {
        Set<? extends Field> fields = Reflection.getFieldsWithAnnotation(type, Column.class);
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column.name().equals(name)) {
                return field.getName();
            }
        }

        throw new IllegalArgumentException("no such column: " + name);
    }
}
