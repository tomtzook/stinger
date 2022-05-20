package stinger.storage.impl;


import stinger.storage.StandardProductType;
import stingerlib.storage.Product;
import stingerlib.storage.ProductType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class FileProduct implements Product {

    private final Path mPath;
    private final ProductType mProductType;

    public FileProduct(Path path, ProductType productType) {
        mPath = path;
        mProductType = productType;
    }

    public FileProduct(File file, ProductType productType) {
        this(file.toPath(), productType);
    }

    public FileProduct(Path path) {
        this(path, StandardProductType.FILE);
    }

    public FileProduct(File file) {
        this(file.toPath());
    }

    @Override
    public ProductType getType() {
        return mProductType;
    }

    @Override
    public InputStream open() throws IOException {
        return new FileInputStream(mPath.toFile());
    }
}
