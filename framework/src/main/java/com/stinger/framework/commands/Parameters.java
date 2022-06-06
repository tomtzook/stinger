package com.stinger.framework.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Parameters {

    public static class Builder {

        private final Map<String, Object> mParameters;

        public Builder() {
            mParameters = new HashMap<>();
        }

        public Builder putInt(String key, int value) {
            mParameters.put(key, value);
            return this;
        }

        public Builder putDouble(String key, double value) {
            mParameters.put(key, value);
            return this;
        }

        public Builder putString(String key, String value) {
            mParameters.put(key, value);
            return this;
        }

        public Parameters build() {
            return new Parameters(mParameters);
        }
    }

    private final Map<String, Object> mParameters;

    public Parameters(Map<String, Object> parameters) {
        mParameters = parameters;
    }

    public int count() {
        return mParameters.size();
    }

    public <T> T get(String key, Class<T> type) throws ParamNotFoundException, ParamTypeMismatchException {
        Object value = mParameters.get(key);
        if (value == null) {
            throw new ParamNotFoundException(key);
        }
        if (!type.isInstance(value)) {
            throw new ParamTypeMismatchException(key, value.getClass(), type);
        }

        return type.cast(value);
    }

    public int getInt(String key) throws ParamNotFoundException, ParamTypeMismatchException {
        return get(key, Integer.class);
    }

    public double getDouble(String key) throws ParamNotFoundException, ParamTypeMismatchException {
        return get(key, Double.class);
    }

    public String getString(String key) throws ParamNotFoundException, ParamTypeMismatchException {
        return get(key, String.class);
    }

    public Map<String, Object> getAllParameters() {
        return Collections.unmodifiableMap(mParameters);
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "mParameters=" + mParameters +
                '}';
    }
}
