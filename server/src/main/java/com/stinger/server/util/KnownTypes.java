package com.stinger.server.util;

import java.util.Collection;

public interface KnownTypes<T extends GenericType<KEY>, KEY> {

    Collection<? extends T> getAll();
    T getFromKey(KEY key);
}
