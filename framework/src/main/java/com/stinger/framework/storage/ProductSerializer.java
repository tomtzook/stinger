package com.stinger.framework.storage;

import com.stinger.framework.util.IoStreams;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class ProductSerializer {

    private final Function<Integer, ProductType> mProductClassifier;

    public ProductSerializer(Function<Integer, ProductType> productClassifier) {
        mProductClassifier = productClassifier;
    }

    public void serialize(DataOutput output, StoredProduct product) throws IOException {
        byte[] data;
        try (InputStream inputStream = product.open()) {
            data = IoStreams.readAll(inputStream);
        }

        output.writeUTF(product.getId());
        output.writeInt(product.getType().intValue());
        output.writeInt(data.length);
        output.write(data);
    }

    public StoredProduct deserialize(DataInput input) throws IOException {
        String id = input.readUTF();

        int typeInt = input.readInt();
        ProductType productType = mProductClassifier.apply(typeInt);

        int dataSize = input.readInt();
        byte[] data = new byte[dataSize];
        input.readFully(data);

        return new InMemoryStoredProduct(id, productType, data);
    }
}
