package stinger.apps;

import java.io.IOException;
import java.nio.file.Path;

public interface AppEnvironment {

    Path getCodeDirectory();
    Path getDataDirectory();

    Path getMainCodeFile();

    void delete() throws IOException;
}
