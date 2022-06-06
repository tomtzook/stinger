package stinger.apps.control;

import com.stinger.framework.logging.Logger;
import stinger.StingerEnvironment;
import stinger.apps.control.ops.AppOperation;
import stinger.apps.control.ops.OperationType;
import stinger.apps.control.ops.OperationsQueue;
import stinger.apps.control.ops.OperationsTask;
import stinger.apps.control.store.AppCreationTransaction;
import stinger.apps.control.store.AppStorage;
import stinger.modules.TaskModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class AppControlModule extends TaskModule {

    private final AppStorage mAppStorage;
    private final OperationsQueue mQueue;
    private final Logger mLogger;

    public AppControlModule(ExecutorService executorService,
                            AppStorage appStorage, OperationsQueue queue, Logger logger) {
        super("apps-module", executorService);
        mAppStorage = appStorage;
        mQueue = queue;
        mLogger = logger;
    }

    public void install(AppConfig config, InputStream codeStream) throws IOException {
        mLogger.info("Starting app installation (id=%d, version=%s)",
                config.getId(), config.getVersion());
        try (AppCreationTransaction transaction = mAppStorage.addProduct(config)) {
            transaction.placeAtEnvironment(codeStream);

            transaction.commit();
            mLogger.info("App installed in environment");
        }
    }

    public void uninstall(int id) {
        mLogger.info("Starting app uninstall %d", id);
        mQueue.queue(new AppOperation(id, OperationType.UNINSTALL));
    }

    public void stopAllApps() {
        try {
            for (InstalledApp app : mAppStorage.getAllInstalled()) {
                mQueue.queue(new AppOperation(app.getConfig().getId(), OperationType.STOP));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitUntilOpQueueIsEmpty(long timeoutMs) throws TimeoutException, InterruptedException {
        mQueue.waitUntilEmpty(timeoutMs);
    }

    @Override
    protected Runnable createTask(StingerEnvironment environment) {
        return new OperationsTask(mAppStorage, mQueue, environment);
    }
}
