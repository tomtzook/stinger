package com.stinger.server.storage;

import com.stinger.framework.logging.Logger;
import com.stinger.framework.storage.ProductJsonSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ToolStorage {

    private final Path mStorageDir;
    private final Logger mLogger;
    private final ProductJsonSerializer mProductJsonSerializer;

    public ToolStorage(Path storageDir, Logger logger, ProductJsonSerializer productJsonSerializer) {
        mStorageDir = storageDir;
        mLogger = logger;
        mProductJsonSerializer = productJsonSerializer;
    }

    public Storage getStorageForTool(String toolId) throws IOException {
        if (toolId == null || toolId.isEmpty()) {
            toolId = "__unknown";
        }

        Path path = mStorageDir.resolve(toolId);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        return new Storage(path, mLogger, mProductJsonSerializer);
    }
}
