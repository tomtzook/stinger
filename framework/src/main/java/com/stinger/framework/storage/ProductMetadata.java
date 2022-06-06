package com.stinger.framework.storage;

import java.util.Map;

public interface ProductMetadata {

    String getId();
    ProductType getType();
    int getPriority();
    long getContentSize();

    Object getProperty(String name);
    <T> T getProperty(String name, Class<T> type);
    boolean hasProperty(String name);
    Map<String, Object> getAllProperties();
}
