package stinger.storage.impl;


import com.stinger.framework.storage.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class FileProduct implements Product {

    private final Path mPath;

    public FileProduct(Path path) {
        mPath = path;
    }

    public FileProduct(File file) {
        this(file.toPath());
    }

    @Override
    public InputStream open() throws IOException {
        return new FileInputStream(mPath.toFile());
    }
}
