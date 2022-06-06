package com.stinger.framework.storage;

import java.util.Set;

public interface ProductMetadata {

    String getId();
    ProductType getType();
    int getPriority();

    Object getProperty(String name);
    <T> T getProperty(String name, Class<T> type);
    boolean hasProperty(String name);
    Set<String> getAllPropertyNames();
}
