package stinger.storage.impl;

import com.stinger.framework.storage.Product;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BinaryProduct implements Product {

    private final byte[] mData;

    public BinaryProduct(byte[] data) {
        mData = data;
    }

    public BinaryProduct(String data) {
        this(data.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public InputStream open() throws IOException {
        return new ByteArrayInputStream(mData);
    }
}
