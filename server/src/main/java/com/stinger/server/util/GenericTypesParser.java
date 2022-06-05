package com.stinger.server.util;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class GenericTypesParser<T extends GenericType<KEY>, KEY> {

    private final Class<T[]> mTypeClass;
    private final Gson mGson;

    private GenericTypesParser(Object array) {
        mTypeClass = (Class<T[]>) array.getClass();
        mGson = new Gson();
    }

    public GenericTypesParser(Class<T> typeClass) {
        this(Array.newInstance(typeClass, 0));
    }

    public KnownTypes<T, KEY> parseFromFile(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            T[] defs = mGson.fromJson(reader, mTypeClass);
            return new GenericKnownTypes<>(Arrays.asList(defs));
        }
    }
}
