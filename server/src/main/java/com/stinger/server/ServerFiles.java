package com.stinger.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerFiles {

    private final Path mStingerDir;

    public ServerFiles(Path stingerDir) {
        mStingerDir = stingerDir;
    }

    public ServerFiles() {
        this(Paths.get(System.getProperty("user.dir")));
    }

    public Path getRoot() {
        return mStingerDir;
    }

    public Path getLogFile() {
        return mStingerDir.resolve("server.log");
    }

    public Path getStorageRoot() throws IOException {
        return existingDirectory("storage");
    }

    public Path getCommandsDirectory() throws IOException {
        return existingDirectory("commands_out");
    }

    public Path getCommandTypesFile() {
        return mStingerDir.resolve("command.types.json");
    }

    public Path getProductTypesFile() {
        return mStingerDir.resolve("product.types.json");
    }

    private Path existingDirectory(String name) throws IOException {
        Path directory = mStingerDir.resolve(name);
        Files.createDirectories(directory);
        return directory;
    }
}
