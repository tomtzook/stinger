package com.stinger.framework.data;

public interface GenericType<KEY> {

    boolean matchesKey(KEY key);
}
