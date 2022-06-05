package com.stinger.server.storage;

import com.google.gson.JsonElement;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.stinger.framework.logging.Logger;
import com.stinger.framework.storage.ProductJsonSerializer;
import com.stinger.framework.storage.ProductMetadata;
import com.stinger.framework.storage.StoredProduct;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

public class Storage {

    private final Path mStorageDir;
    private final Logger mLogger;
    private final ProductJsonSerializer mProductJsonSerializer;

    public Storage(Path storageDir, Logger logger, ProductJsonSerializer productJsonSerializer) {
        mStorageDir = storageDir;
        mLogger = logger;
        mProductJsonSerializer = productJsonSerializer;
    }

    public void save(StoredProduct product) {
        Path dataFile = mStorageDir.resolve(String.format("%s.%s",
                product.getMetadata().getId(),
                product.getMetadata().getType().name()));
        Path metaFile = mStorageDir.resolve(String.format("%s.%s.metadata",
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
        JsonElement element = mProductJsonSerializer.serialize(metadata);
        Files.write(file, Collections.singleton(element.toString()),
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }
}
