package com.stinger.framework.app;

import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class AppPackage {

    private final Zip mZip;

    public AppPackage(Zip zip) {
        mZip = zip;
    }

    public static AppPackage fromFile(Path path) {
        return new AppPackage(Zip.fromPath(path));
    }

    public AppConf getConf() throws IOException {
        try (OpenZip openZip = mZip.open()) {
            Path manifestPath = openZip.getPath("META-INF", "MANIFEST.MF");
            try (InputStream inputStream = Files.newInputStream(manifestPath)) {
                Manifest manifest = new Manifest(inputStream);
                Attributes attributes = manifest.getMainAttributes();
                int id = Integer.parseInt(attributes.getValue("app-id"));
                String version = attributes.getValue("app-version");

                return new AppConf(id, version);
            }
        }
    }
}
