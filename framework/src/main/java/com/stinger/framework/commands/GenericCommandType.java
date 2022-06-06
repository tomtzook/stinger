package com.stinger.framework.commands;

import com.stinger.framework.data.GenericType;

public class GenericCommandType implements CommandType, GenericType<Integer> {

    private final String mName;
    private final int mIntValue;

    public GenericCommandType(String name, int intValue) {
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
        return "GenericCommandType{" +
                "mName='" + mName + '\'' +
                ", mIntValue=" + mIntValue +
                '}';
    }
}
