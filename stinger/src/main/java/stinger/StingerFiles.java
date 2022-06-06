package stinger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StingerFiles {

    private final Path mStingerDir;

    public StingerFiles(Path stingerDir) {
        mStingerDir = stingerDir;
    }

    public StingerFiles() {
        this(Paths.get(System.getProperty("user.dir")));
    }

    public Path getRoot() {
        return mStingerDir;
    }

    public Path getLogFile() {
        return mStingerDir.resolve("lgbt");
    }

    public Path getStorageRoot() throws IOException {
        return existingDirectory("str");
    }

    public Path getAppsRoot() throws IOException {
        return existingDirectory("approot");
    }

    private Path existingDirectory(String name) throws IOException {
        Path directory = mStingerDir.resolve(name);
        Files.createDirectories(directory);
        return directory;
    }
}
