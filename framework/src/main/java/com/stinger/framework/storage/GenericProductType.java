package com.stinger.framework.storage;

import com.stinger.framework.data.GenericType;

public class GenericProductType implements ProductType, GenericType<Integer> {

    private final String mName;
    private final int mIntValue;

    public GenericProductType(String name, int intValue) {
        mName = name;
        mIntValue = intValue;
    }

    @Override
    public String name() {
        return mName;
    }

    @Override
    public int intValue() {
        return mIntValue;
    }

    @Override
    public boolean matchesKey(Integer key) {
        return mIntValue == key;
    }

    @Override
    public String toString() {
        return "GenericProductType{" +
                "mName='" + mName + '\'' +
                ", mIntValue=" + mIntValue +
                '}';
    }
}
