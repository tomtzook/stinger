package com.stinger.framework.storage;

public interface WritableProductMetadata extends ProductMetadata {

    <T> void putProperty(String name, T value);
}
