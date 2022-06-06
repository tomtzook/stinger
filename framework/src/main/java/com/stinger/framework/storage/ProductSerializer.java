package com.stinger.framework.storage;

import com.stinger.framework.util.IoStreams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ProductSerializer {

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

        Set<String> propertyNames = metadata.getAllPropertyNames();
        output.writeInt(propertyNames.size());

        for (String name : propertyNames) {
            Object value = metadata.getProperty(name);
            output.writeUTF(name);
            serializeTyped(output, value);
        }
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

        Map<String, Object> properties = new HashMap<>();
        int propertyCount = input.readInt();
        for (int i = 0; i < propertyCount; i++) {
            String name = input.readUTF();
            Object value = readTyped(input);

            properties.put(name, value);
        }

        return new GenericProductMetadata(id, productType, priority, properties);
    }

    private void serializeTyped(DataOutput output, Object value) throws IOException {
        if (value == null) {
            output.writeInt(SerializedType.NULL.intValue());
        } else if (value instanceof Integer) {
            output.writeInt(SerializedType.INTEGER.intValue());
            output.writeInt((Integer) value);
        } else if (value instanceof Double) {
            output.writeInt(SerializedType.DOUBLE.intValue());
            output.writeDouble((Double) value);
        } else if (value instanceof String) {
            output.writeInt(SerializedType.STRING.intValue());
            output.writeUTF((String) value);
        } else if (value instanceof Boolean) {
            output.writeInt(SerializedType.BOOLEAN.intValue());
            output.writeBoolean((Boolean) value);
        } else {
            throw new IOException("unsupported type: " + value.getClass().getName());
        }
    }

    private Object readTyped(DataInput input) throws IOException {
        int typeInt = input.readInt();
        SerializedType type = SerializedType.fromInt(typeInt);

        switch (type) {
            case NULL: return null;
            case INTEGER: return input.readInt();
            case DOUBLE: return input.readDouble();
            case STRING: return input.readUTF();
            case BOOLEAN: return input.readBoolean();
            default: throw new IOException("unsupported type: " + type.name());
        }
    }
}
