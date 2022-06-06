package com.stinger.framework.storage;

import java.util.HashMap;
import java.util.Map;

public class GenericWritableProductMetadata extends GenericProductMetadata
        implements WritableProductMetadata {

    private final Map<String, Object> mProperties;

    public GenericWritableProductMetadata(String id, ProductType type,
                                          int priority,
                                          long contentSize,
                                          Map<String, Object> properties) {
        super(id, type, priority, contentSize, properties);
        mProperties = properties;
    }

    public GenericWritableProductMetadata(String id, ProductType type,
                                          int priority,
                                          long contentSize) {
        this(id, type, priority, contentSize, new HashMap<>());
    }

    @Override
    public void setContentSize(long size) {
        mContentSize = size;
    }

    @Override
    public <T> void putProperty(String name, T value) {
        mProperties.put(name, value);
    }
}
