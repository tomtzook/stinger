package stinger.apps.control;

import stinger.apps.AppEnvironment;
import stinger.util.FileHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultAppEnvironment implements AppEnvironment {

    private final Path mRoot;

    public DefaultAppEnvironment(Path root) {
        mRoot = root;
    }

    public static DefaultAppEnvironment create(Path appRoot) throws IOException {
        if (!Files.exists(appRoot)) {
            Files.createDirectories(appRoot);
        }

        DefaultAppEnvironment environment = new DefaultAppEnvironment(appRoot);

        if (!Files.exists(environment.getCodeDirectory())) {
            Files.createDirectory(environment.getCodeDirectory());
        }
        if (!Files.exists(environment.getDataDirectory())) {
            Files.createDirectory(environment.getDataDirectory());
        }

        return environment;
    }

    @Override
    public Path getCodeDirectory() {
        return mRoot.resolve("code");
    }

    @Override
    public Path getDataDirectory() {
        return mRoot.resolve("data");
    }

    @Override
    public Path getMainCodeFile() {
        return getCodeDirectory().resolve("app.jar");
    }

    @Override
    public void delete() throws IOException {
        FileHelper.recursiveDelete(mRoot);
    }
}
