package com.stinger.framework.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class GenericProductMetadata implements ProductMetadata {

    private final String mId;
    private final ProductType mType;
    private final int mPriority;
    protected long mContentSize;
    private final Map<String, Object> mProperties;

    public GenericProductMetadata(String id, ProductType type,
                                  int priority,
                                  long contentSize,
                                  Map<String, Object> properties) {
        mId = id;
        mType = type;
        mPriority = priority;
        mContentSize = contentSize;
        mProperties = properties;
    }

    public GenericProductMetadata(String id, ProductType type,
                                  int priority,
                                  long contentSize) {
        this(id, type, priority, contentSize, new HashMap<>());
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
    public long getContentSize() {
        return mContentSize;
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
    public Map<String, Object> getAllProperties() {
        return Collections.unmodifiableMap(mProperties);
    }
}
