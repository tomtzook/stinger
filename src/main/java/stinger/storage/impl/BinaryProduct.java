package stinger.storage.impl;

import stinger.storage.StandardProductType;
import stingerlib.storage.Product;
import stingerlib.storage.ProductType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BinaryProduct implements Product {

    private final byte[] mData;
    private final ProductType mProductType;

    public BinaryProduct(byte[] data, ProductType productType) {
        mData = data;
        mProductType = productType;
    }

    public BinaryProduct(String data, ProductType productType) {
        this(data.getBytes(StandardCharsets.UTF_8), productType);
    }

    public BinaryProduct(byte[] data) {
        this(data, StandardProductType.BLOB);
    }

    public BinaryProduct(String data) {
        this(data.getBytes(StandardCharsets.UTF_8));
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
