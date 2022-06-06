package stinger.apps.control.store;

import com.castle.util.closeables.Closeables;
import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Database;
import com.stinger.framework.db.Transaction;
import com.stinger.framework.db.hibernate.JpaDatabase;
import stinger.apps.AppEnvironment;
import stinger.apps.ExecutionState;
import stinger.apps.ExecutionType;
import stinger.apps.InstallationState;
import stinger.apps.StingerAppControl;
import stinger.apps.control.AppConfig;
import stinger.apps.control.AppLoader;
import stinger.apps.control.DefaultAppEnvironment;
import stinger.apps.control.InstalledApp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppStorage {

    private final Database mDatabase;
    private final Path mAppsRoot;
    private final AppLoader mAppLoader;

    public AppStorage(Database database, Path appsRoot, AppLoader appLoader) {
        mDatabase = database;
        mAppsRoot = appsRoot;
        mAppLoader = appLoader;
    }

    public static AppStorage fromConfig(String configName, Path appsRoot, AppLoader appLoader) {
        Database database = new JpaDatabase(configName);
        return new AppStorage(database, appsRoot, appLoader);
    }

    public AppCreationTransaction addProduct(AppConfig config) throws IOException {
        Connection connection = mDatabase.open();
        try {
            Transaction transaction = connection.openTransaction();
            try {
                if (transaction.getByIdentity(config.getId(), StoredAppModel.class).isPresent()) {
                    throw new IOException("App " + config.getId() + " already exists");
                }

                Path appRoot = mAppsRoot.resolve(String.valueOf(config.getId()));

                StoredAppModel model = new StoredAppModel();
                model.setAppId(config.getId());
                model.setVersion(config.getVersion());
                model.setAppPath(appRoot.toAbsolutePath().toString());
                model.setExecutionType(ExecutionType.LOCAL_THREAD);
                model.setInstallationState(InstallationState.UNINSTALLED);
                model.setExecutionState(ExecutionState.NOT_RUNNING);

                transaction.add(model);

                return new AppCreationTransaction(connection, transaction, model);
            } catch (RuntimeException | Error | IOException e) {
                Closeables.silentClose(transaction);
                throw e;
            }
        } catch (RuntimeException | Error | IOException e) {
            Closeables.silentClose(connection);
            throw e;
        }
    }

    public void delete(int id) throws IOException {
        try (Connection connection = mDatabase.open();
             Transaction transaction = connection.openTransaction()) {
            Optional<StoredAppModel> optional = transaction.getByIdentity(id, StoredAppModel.class);
            if (optional.isEmpty()) {
                throw new IOException("no such app: " + id);
            }

            StoredAppModel model = optional.get();
            if (model.getExecutionState() == ExecutionState.RUNNING) {
                throw new IOException("app still running");
            }
            if (model.getInstallationState() != InstallationState.UNINSTALL_HAS_ENVIRONMENT) {
                throw new IOException("app still not up");
            }

            try {
                AppEnvironment environment = model.getEnvironment();
                environment.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }

            transaction.delete(model);
            transaction.commit();
        }
    }

    public AppExecutionTransaction openForExecution(int id) throws IOException {
        Connection connection = mDatabase.open();
        try {
            Transaction transaction = connection.openTransaction();
            try {
                Optional<StoredAppModel> optional = transaction.getByIdentity(id, StoredAppModel.class);
                if (optional.isEmpty()) {
                    throw new IOException("no such app: " + id);
                }

                StoredAppModel model = optional.get();
                StingerAppControl app = mAppLoader.loadApp(
                        model.getAppId(),
                        model.getExecutionType(),
                        model.getEnvironment());

                return new AppExecutionTransaction(connection, transaction,
                        model, app);
            } catch (RuntimeException | Error | IOException e) {
                Closeables.silentClose(transaction);
                throw e;
            }
        } catch (RuntimeException | Error | IOException e) {
            Closeables.silentClose(connection);
            throw e;
        }
    }

    public Optional<InstalledApp> getById(int id) throws IOException {
        try (Connection connection = mDatabase.open();
             Transaction transaction = connection.openTransaction()) {
            Optional<StoredAppModel> optional = transaction.getByIdentity(id, StoredAppModel.class);
            if (optional.isEmpty()) {
                return Optional.empty();
            }

            StoredAppModel model = optional.get();
            InstalledApp app = new InstalledApp(
                    new AppConfig(model.getAppId(), model.getVersion()),
                    DefaultAppEnvironment.create(Paths.get(model.getAppPath()))
            );

            return Optional.of(app);
        }
    }

    public List<InstalledApp> getAll() throws IOException {
        try (Connection connection = mDatabase.open();
             Transaction transaction = connection.openTransaction()) {
            List<InstalledApp> list = new ArrayList<>();
            for (StoredAppModel model : transaction.getAll(StoredAppModel.class)) {
                InstalledApp app = new InstalledApp(
                        new AppConfig(model.getAppId(), model.getVersion()),
                        DefaultAppEnvironment.create(Paths.get(model.getAppPath()))
                );
                list.add(app);
            }

            return list;
        }
    }

    public List<InstalledApp> getAllInstalledAtExecutionState(ExecutionState state) throws IOException {
        try (Connection connection = mDatabase.open();
             Transaction transaction = connection.openTransaction()) {
            List<StoredAppModel> models = transaction.select(StoredAppModel.class)
                    .where("install_state", InstallationState.INSTALLED)
                    .where("exec_state", state)
                    .getAll();

            List<InstalledApp> list = new ArrayList<>(models.size());
            for (StoredAppModel model : models) {
                InstalledApp app = new InstalledApp(
                        new AppConfig(model.getAppId(), model.getVersion()),
                        DefaultAppEnvironment.create(Paths.get(model.getAppPath()))
                );
                list.add(app);
            }

            return list;
        }
    }

    public List<InstalledApp> getAllInstalled() throws IOException {
        try (Connection connection = mDatabase.open();
             Transaction transaction = connection.openTransaction()) {
            List<StoredAppModel> models = transaction.select(StoredAppModel.class)
                    .where("install_state", InstallationState.INSTALLED)
                    .getAll();

            List<InstalledApp> list = new ArrayList<>(models.size());
            for (StoredAppModel model : models) {
                InstalledApp app = new InstalledApp(
                        new AppConfig(model.getAppId(), model.getVersion()),
                        DefaultAppEnvironment.create(Paths.get(model.getAppPath()))
                );
                list.add(app);
            }

            return list;
        }
    }

    public List<InstalledApp> getAllNotFullyInstalled() throws IOException {
        try (Connection connection = mDatabase.open();
             Transaction transaction = connection.openTransaction()) {
            List<StoredAppModel> models = transaction.select(StoredAppModel.class)
                    .where("install_state", InstallationState.ENVIRONMENT_MADE)
                    .getAll();

            List<InstalledApp> list = new ArrayList<>(models.size());
            for (StoredAppModel model : models) {
                InstalledApp app = new InstalledApp(
                        new AppConfig(model.getAppId(), model.getVersion()),
                        DefaultAppEnvironment.create(Paths.get(model.getAppPath()))
                );
                list.add(app);
            }

            return list;
        }
    }
}
