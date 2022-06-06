package com.stinger.framework.data;

import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TypedJsonSerializer {

    public void writeTypedMap(JsonTreeWriter writer, Map<String, Object> map) throws IOException {
        writer.beginObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            writer.name(entry.getKey());
            writeTyped(writer, entry.getValue());
        }
        writer.endObject();
    }

    public Map<String, Object> readTypedMap(JsonTreeReader reader) throws IOException {
        Map<String, Object> map = new HashMap<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            Object value = readTyped(reader);
            map.put(name, value);
        }
        reader.endObject();

        return map;
    }

    public void writeTyped(JsonTreeWriter writer, Object value) throws IOException {
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
        } else if (value instanceof byte[]) {
            writer.beginObject();
            writer.name("type").value(SerializedType.BLOB.intValue());
            writer.name("value").value(Base64.getEncoder().encodeToString((byte[]) value));
            writer.endObject();
        } else {
            throw new IOException("unsupported type: " + value.getClass().getName());
        }
    }

    public Object readTyped(JsonTreeReader reader) throws IOException {
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
            case BLOB: {
                String str = reader.nextString();
                return Base64.getDecoder().decode(str);
            }
            default: throw new IOException("unsupported type: " + type.name());
        }

        reader.endObject();

        return value;
    }

    protected void expectName(JsonTreeReader reader, String expected) throws IOException {
        String name = reader.nextName();
        if (!name.equals(expected)) {
            throw new IOException("expected '" + expected + "'");
        }
    }
}
