package com.stinger.framework.storage;

public interface WritableProductMetadata extends ProductMetadata {

    void setContentSize(long size);

    <T> void putProperty(String name, T value);
}
