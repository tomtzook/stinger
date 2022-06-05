package com.stinger.framework.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InMemoryStoredProduct implements StoredProduct {

    private final ProductMetadata mMetadata;
    private final byte[] mData;

    public InMemoryStoredProduct(ProductMetadata metadata, byte[] data) {
        mMetadata = metadata;
        mData = data;
    }

    @Override
    public ProductMetadata getMetadata() {
        return mMetadata;
    }

    @Override
    public InputStream open() throws IOException {
        return new ByteArrayInputStream(mData);
    }
}
