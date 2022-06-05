package com.stinger.framework.storage;

import com.google.gson.JsonElement;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ProductJsonSerializer {

    private final Function<Integer, ProductType> mProductClassifier;

    public ProductJsonSerializer(Function<Integer, ProductType> productClassifier) {
        mProductClassifier = productClassifier;
    }

    public JsonElement serialize(ProductMetadata metadata) throws IOException {
        try (JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name("id").value(metadata.getId());
            writer.name("type").value(metadata.getType().name());
            writer.name("typeInt").value(metadata.getType().intValue());
            writeProps(writer, metadata);
            writer.endObject();

            return writer.get();
        }
    }

    public ProductMetadata deserialize(JsonElement element) throws IOException {
        try (JsonTreeReader reader = new JsonTreeReader(element)) {
            reader.beginObject();

            expectName(reader, "id");
            String id = reader.nextString();
            expectName(reader, "type");
            reader.nextString();
            expectName(reader, "typeInt");
            int typeInt = reader.nextInt();
            ProductType type = mProductClassifier.apply(typeInt);


            Map<String, Object> props = readProps(reader);
            reader.endObject();

            return new GenericProductMetadata(id, type, props);
        }
    }

    private void writeProps(JsonTreeWriter writer, ProductMetadata metadata) throws IOException {
        writer.beginObject();
        for (String name : metadata.getAllPropertyNames()) {
            Object value = metadata.getProperty(name);

            writer.name(name);
            writeTyped(writer, value);
        }
        writer.endObject();
    }

    private Map<String, Object> readProps(JsonTreeReader reader) throws IOException {
        Map<String, Object> props = new HashMap<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            Object value = readTyped(reader);
            props.put(name, value);
        }
        reader.endObject();

        return props;
    }

    private void writeTyped(JsonTreeWriter writer, Object value) throws IOException {
        if (value == null) {
            writer.beginObject();
            writer.name("type").value(SerializedType.NULL.intValue());
            writer.name("value").nullValue();
            writer.endObject();
        } else if (value instanceof Integer) {
            writer.beginObject();
            writer.name("type").value(SerializedType.INTEGER.intValue());
            writer.name("value").value((Integer) value);
            writer.endObject();
        } else if (value instanceof Double) {
            writer.beginObject();
            writer.name("type").value(SerializedType.DOUBLE.intValue());
            writer.name("value").value((Double) value);
            writer.endObject();
        } else if (value instanceof String) {
            writer.beginObject();
            writer.name("type").value(SerializedType.STRING.intValue());
            writer.name("value").value((String) value);
            writer.endObject();
        } else if (value instanceof Boolean) {
            writer.beginObject();
            writer.name("type").value(SerializedType.BOOLEAN.intValue());
            writer.name("value").value((Boolean) value);
            writer.endObject();
        } else {
            throw new IOException("unsupported type: " + value.getClass().getName());
        }
    }

    private Object readTyped(JsonTreeReader reader) throws IOException {
        reader.beginObject();

        expectName(reader, "type");
        SerializedType type = SerializedType.fromInt(reader.nextInt());
        expectName(reader, "value");

        Object value;
        switch (type) {
            case NULL: {
                reader.nextNull();
                value = null;
                break;
            }
            case INTEGER: {
                value = reader.nextInt();
                break;
            }
            case DOUBLE: {
                value = reader.nextDouble();
                break;
            }
            case STRING: {
                value = reader.nextString();
                break;
            }
            case BOOLEAN: {
                value = reader.nextBoolean();
                break;
            }
            default: throw new IOException("unsupported type: " + type.name());
        }

        reader.endObject();

        return value;
    }

    private void expectName(JsonTreeReader reader, String expected) throws IOException {
        String name = reader.nextName();
        if (!name.equals(expected)) {
            throw new IOException("expected '" + expected + "'");
        }
    }
}
