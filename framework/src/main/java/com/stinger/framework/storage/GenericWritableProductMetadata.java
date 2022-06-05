package com.stinger.framework.storage;

import java.util.HashMap;
import java.util.Map;

public class GenericWritableProductMetadata extends GenericProductMetadata
        implements WritableProductMetadata {

    private final Map<String, Object> mProperties;

    public GenericWritableProductMetadata(String id, ProductType type, Map<String, Object> properties) {
        super(id, type, properties);
        mProperties = properties;
    }

    public GenericWritableProductMetadata(String id, ProductType type) {
        this(id, type, new HashMap<>());
    }

    @Override
    public <T> void putProperty(String name, T value) {
        mProperties.put(name, value);
    }
}
