package stinger.apps.control.store;

import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Transaction;
import stinger.apps.AppEnvironment;
import stinger.apps.InstallationState;
import stinger.util.IoStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class AppCreationTransaction extends AppTransaction {

    private final StoredAppModel mModel;

    public AppCreationTransaction(Connection connection, Transaction transaction, StoredAppModel model) {
        super(connection, transaction);
        mModel = model;
    }

    public AppEnvironment placeAtEnvironment(InputStream codeStream) throws IOException {
        AppEnvironment environment = mModel.getEnvironment(true);
        try (OutputStream outputStream = Files.newOutputStream(environment.getMainCodeFile())) {
            IoStreams.copy(codeStream, outputStream);
        }

        mModel.setInstallationState(InstallationState.ENVIRONMENT_MADE);

        return environment;
    }
}
