package com.stinger.server.storage;

import com.google.gson.JsonElement;
import com.stinger.framework.data.TypedJsonSerializer;
import com.stinger.framework.logging.Logger;
import com.stinger.framework.storage.ProductJsonSerializer;
import com.stinger.framework.storage.ProductMetadata;
import com.stinger.framework.storage.StoredProduct;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.Map;

public class Storage {

    private final Path mStorageDir;
    private final Path mToolMetaDir;
    private final Logger mLogger;
    private final ProductJsonSerializer mProductJsonSerializer;

    public Storage(Path storageDir, Logger logger, ProductJsonSerializer productJsonSerializer)
            throws IOException {
        mStorageDir = storageDir.resolve("products");
        if (!Files.exists(mStorageDir)) {
            Files.createDirectory(mStorageDir);
        }

        mToolMetaDir = storageDir.resolve("meta");
        if (!Files.exists(mToolMetaDir)) {
            Files.createDirectory(mToolMetaDir);
        }

        mLogger = logger;
        mProductJsonSerializer = productJsonSerializer;
    }

    public void storeToolMeta(Map<String, Object> data) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.
                ofPattern("uuuu-MM-dd;kk_mm_ss");

        String dateString = LocalDateTime.now().format(dateTimeFormatter);
        Path file = mToolMetaDir.resolve(String.format("%s.meta", dateString));
        int version = 1;
        while (Files.exists(file)) {
            file = mToolMetaDir.resolve(String.format("%s.%d.meta", dateString, version));
            version++;
        }

        TypedJsonSerializer serializer = new TypedJsonSerializer();
        JsonElement element = serializer.writeTypedMap(data);
        Files.write(file, element.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void save(StoredProduct product) {
        Path dataFile = mStorageDir.resolve(String.format("%d.%s.%s",
                product.getMetadata().getPriority(),
                product.getMetadata().getId(),
                product.getMetadata().getType().name()));
        Path metaFile = mStorageDir.resolve(String.format("%d.%s.%s.metadata",
                product.getMetadata().getPriority(),
                product.getMetadata().getId(),
                product.getMetadata().getType().name()));

        try {
            try (InputStream inputStream = product.open()) {
                Files.copy(inputStream, dataFile);
            }
            saveMetadata(product.getMetadata(), metaFile);
        }  catch (IOException e) {
            mLogger.error("Storage error", e);
        }
    }

    private void saveMetadata(ProductMetadata metadata, Path file) throws IOException {
        JsonElement element = mProductJsonSerializer.serializeMetadata(metadata);
        Files.write(file, Collections.singleton(element.toString()),
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }
}
