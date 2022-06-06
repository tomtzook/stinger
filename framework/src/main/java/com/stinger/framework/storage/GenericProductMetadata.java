package com.stinger.framework.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class GenericProductMetadata implements ProductMetadata {

    private final String mId;
    private final ProductType mType;
    private final int mPriority;
    private final Map<String, Object> mProperties;

    public GenericProductMetadata(String id, ProductType type, int priority,
                                  Map<String, Object> properties) {
        mId = id;
        mType = type;
        mPriority = priority;
        mProperties = properties;
    }

    public GenericProductMetadata(String id, int priority, ProductType type) {
        this(id, type, priority, new HashMap<>());
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public ProductType getType() {
        return mType;
    }

    @Override
    public int getPriority() {
        return mPriority;
    }

    @Override
    public Object getProperty(String name) {
        if (!hasProperty(name)) {
            throw new NoSuchElementException(name);
        }

        return mProperties.get(name);
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        Object value = getProperty(name);
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(name + " is not of type " + type.getName());
        }

        return type.cast(value);
    }

    @Override
    public boolean hasProperty(String name) {
        return mProperties.containsKey(name);
    }

    @Override
    public Set<String> getAllPropertyNames() {
        return mProperties.keySet();
    }
}
