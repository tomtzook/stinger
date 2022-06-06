package com.stinger.framework.storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class InFileStoredProduct implements StoredProduct {

    private final ProductMetadata mMetadata;
    private final Path mDataPath;

    public InFileStoredProduct(ProductMetadata metadata, Path dataPath) {
        mMetadata = metadata;
        mDataPath = dataPath;
    }

    @Override
    public ProductMetadata getMetadata() {
        return mMetadata;
    }

    @Override
    public InputStream open() throws IOException {
        return new FileInputStream(mDataPath.toFile());
    }
}
