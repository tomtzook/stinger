package com.stinger.framework.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InMemoryStoredProduct implements StoredProduct {

    private final String mId;
    private final ProductType mProductType;
    private final byte[] mData;

    public InMemoryStoredProduct(String id, ProductType productType, byte[] data) {
        mId = id;
        mProductType = productType;
        mData = data;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public ProductType getType() {
        return mProductType;
    }

    @Override
    public InputStream open() throws IOException {
        return new ByteArrayInputStream(mData);
    }
}
