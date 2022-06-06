package com.stinger.framework.storage;

import com.google.gson.JsonElement;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.stinger.framework.data.TypedJsonSerializer;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

public class ProductJsonSerializer extends TypedJsonSerializer {

    private final Function<Integer, ProductType> mProductClassifier;

    public ProductJsonSerializer(Function<Integer, ProductType> productClassifier) {
        mProductClassifier = productClassifier;
    }

    public JsonElement serializeMetadata(ProductMetadata metadata) throws IOException {
        try (JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name("id").value(metadata.getId());
            writer.name("type").value(metadata.getType().name());
            writer.name("typeInt").value(metadata.getType().intValue());
            writer.name("priority").value(metadata.getPriority());
            writer.name("contentSize").value(metadata.getContentSize());
            writer.name("props");
            writeTypedMap(writer, metadata.getAllProperties());
            writer.endObject();

            return writer.get();
        }
    }

    public ProductMetadata deserializeMetadata(JsonElement element) throws IOException {
        try (JsonTreeReader reader = new JsonTreeReader(element)) {
            reader.beginObject();

            expectName(reader, "id");
            String id = reader.nextString();
            expectName(reader, "type");
            reader.nextString();
            expectName(reader, "typeInt");
            int typeInt = reader.nextInt();
            ProductType type = mProductClassifier.apply(typeInt);
            expectName(reader, "priority");
            int priority = reader.nextInt();
            expectName(reader, "contentSize");
            long contentSize = reader.nextLong();

            expectName(reader, "props");
            Map<String, Object> props = readTypedMap(reader);
            reader.endObject();

            return new GenericProductMetadata(id, type, priority, contentSize, props);
        }
    }
}
