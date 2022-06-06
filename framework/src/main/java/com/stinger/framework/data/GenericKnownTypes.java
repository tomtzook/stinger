package com.stinger.framework.data;

import java.util.Collection;

public class GenericKnownTypes<T extends GenericType<KEY>, KEY> implements KnownTypes<T, KEY> {

    private final Collection<? extends T> mTypes;

    public GenericKnownTypes(Collection<? extends T> types) {
        mTypes = types;
    }

    @Override
    public Collection<? extends T> getAll() {
        return mTypes;
    }

    @Override
    public T getFromKey(KEY key) {
        for (T type : mTypes) {
            if (type.matchesKey(key)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown type: " + key);
    }
}
