package com.stinger.framework.storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class InFileStoredProduct implements StoredProduct {

    private final String mId;
    private final ProductType mProductType;
    private final Path mDataPath;

    public InFileStoredProduct(String id, ProductType productType, Path dataPath) {
        mId = id;
        mProductType = productType;
        mDataPath = dataPath;
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
        return new FileInputStream(mDataPath.toFile());
    }

    public Path getDataPath() {
        return mDataPath;
    }
}
