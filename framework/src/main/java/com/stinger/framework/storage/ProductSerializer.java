package com.stinger.framework.storage;

import com.stinger.framework.data.TypedSerializer;
import com.stinger.framework.util.IoStreams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

public class ProductSerializer extends TypedSerializer {

    private final Function<Integer, ProductType> mProductClassifier;

    public ProductSerializer(Function<Integer, ProductType> productClassifier) {
        mProductClassifier = productClassifier;
    }

    public byte[] serialize(StoredProduct product) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            serialize(dataOutputStream, product);

            dataOutputStream.flush();
            return outputStream.toByteArray();
        }
    }

    public void serialize(DataOutput output, StoredProduct product) throws IOException {
        serializeMetadata(output, product.getMetadata());

        try (InputStream inputStream = product.open()) {
            byte[] data = IoStreams.readAll(inputStream);
            output.writeInt(data.length);
            output.write(data);
        }
    }

    public byte[] serializeMetadata(ProductMetadata metadata) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            serializeMetadata(dataOutputStream, metadata);

            dataOutputStream.flush();
            return outputStream.toByteArray();
        }
    }

    public void serializeMetadata(DataOutput output, ProductMetadata metadata) throws IOException {
        output.writeUTF(metadata.getId());
        output.writeInt(metadata.getType().intValue());
        output.writeInt(metadata.getPriority());
        output.writeLong(metadata.getContentSize());
        writeTypedMap(output, metadata.getAllProperties());
    }

    public StoredProduct deserialize(byte[] data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            return deserialize(dataInputStream);
        }
    }

    public StoredProduct deserialize(DataInput input) throws IOException {
        ProductMetadata metadata = deserializeMetadata(input);

        int dataSize = input.readInt();
        byte[] data = new byte[dataSize];
        input.readFully(data);

        return new InMemoryStoredProduct(metadata, data);
    }

    public ProductMetadata deserializeMetadata(byte[] data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            return deserializeMetadata(dataInputStream);
        }
    }

    public ProductMetadata deserializeMetadata(DataInput input) throws IOException {
        String id = input.readUTF();
        int typeInt = input.readInt();
        ProductType productType = mProductClassifier.apply(typeInt);
        int priority = input.readInt();
        long contentSize = input.readLong();
        Map<String, Object> properties = readTypedMap(input);

        return new GenericProductMetadata(id, productType, priority, contentSize, properties);
    }
}
