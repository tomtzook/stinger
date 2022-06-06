package com.stinger.framework.storage;

import java.util.HashMap;
import java.util.Map;

public class GenericWritableProductMetadata extends GenericProductMetadata
        implements WritableProductMetadata {

    private final Map<String, Object> mProperties;

    public GenericWritableProductMetadata(String id, ProductType type, int priority,
                                          Map<String, Object> properties) {
        super(id, type, priority, properties);
        mProperties = properties;
    }

    public GenericWritableProductMetadata(String id, ProductType type, int priority) {
        this(id, type, priority, new HashMap<>());
    }

    @Override
    public <T> void putProperty(String name, T value) {
        mProperties.put(name, value);
    }
}
