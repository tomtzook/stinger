package stinger.apps.control.ops;

import stinger.StingerEnvironment;
import stinger.apps.ExecutionState;
import stinger.apps.control.InstalledApp;
import stinger.apps.control.store.AppStorage;
import stinger.modules.PeriodicTaskModule;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class AppsWatchdogModule extends PeriodicTaskModule {

    private final AppStorage mAppStorage;
    private final OperationsQueue mQueue;

    public AppsWatchdogModule(ExecutorService executorService,
                              AppStorage appStorage, OperationsQueue queue) {
        super("apps-watchdog", executorService, 2 * 1000);
        mAppStorage = appStorage;
        mQueue = queue;
    }

    @Override
    protected Runnable createTask(StingerEnvironment environment) {
        return new Task(mAppStorage, mQueue);
    }

    private static class Task implements Runnable {

        private final AppStorage mAppStorage;
        private final OperationsQueue mQueue;

        private Task(AppStorage appStorage, OperationsQueue queue) {
            mAppStorage = appStorage;
            mQueue = queue;
        }

        @Override
        public void run() {
            finishInstallAllInEnvironment();
            startAllNotRunningApps();
        }

        private void finishInstallAllInEnvironment() {
            try {
                List<InstalledApp> apps = mAppStorage.getAllNotFullyInstalled();
                queueOpForAll(apps, OperationType.INSTALL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void startAllNotRunningApps() {
            try {
                List<InstalledApp> apps = mAppStorage.getAllInstalledAtExecutionState(ExecutionState.NOT_RUNNING);
                queueOpForAll(apps, OperationType.START);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void queueOpForAll(List<InstalledApp> apps, OperationType type) {
            for (InstalledApp app : apps) {
                mQueue.queueIfNoOpQueuedOnApp(new AppOperation(
                        app.getConfig().getId(),
                        type));
            }
        }
    }
}
