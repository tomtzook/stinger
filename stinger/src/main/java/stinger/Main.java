package stinger;

import stinger.apps.control.AppControlModule;
import stinger.apps.control.AppLoader;
import stinger.apps.control.ops.AppsWatchdogModule;
import stinger.apps.control.ops.OperationsQueue;
import stinger.apps.control.store.AppStorage;
import stinger.comm.CommunicationModule;
import stinger.commands.CommandModule;
import stinger.logging.FileLogger;
import stinger.logging.LoggingModule;
import stinger.meta.ToolMetaStore;
import stinger.modules.StingerModules;
import stinger.storage.PersistentStorage;
import stinger.storage.Storage;
import stinger.storage.StorageIndex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Main {

    public static void main(String[] args) {
        loadNatives();

        ThreadFactory factory = new StingerThreadFactory(new ThreadGroup("sst"));
        ExecutorService executorService = Executors.newFixedThreadPool(Constants.CORE_THREAD_POOL_SIZE, factory);
        try {
            StingerFiles files = new StingerFiles();

            ToolMetaStore toolMetaStore = ToolMetaStore.fromConfig("main");

            StingerControl stingerControl = new StringerControlImpl(Thread.currentThread());
            FileLogger logger = new FileLogger(files.getLogFile());
            Storage storage = new PersistentStorage(
                    files.getStorageRoot(),
                    StorageIndex.fromConfig("main", logger)
            );

            AppLoader appLoader = new AppLoader(logger);
            AppStorage appStorage = AppStorage.fromConfig("appstore", files.getAppsRoot(), appLoader);
            OperationsQueue operationsQueue = new OperationsQueue();
            AppControlModule appControlModule =
                    new AppControlModule(executorService, appStorage, operationsQueue, logger);
            AppsWatchdogModule appsWatchdogModule =
                    new AppsWatchdogModule(executorService, appStorage, operationsQueue);

            StingerEnvironmentImpl environment = new StingerEnvironmentImpl(
                    executorService,
                    toolMetaStore,
                    storage,
                    logger,
                    stingerControl);

            Stinger stinger = new Stinger(environment);
            try {
                StingerModules modules = environment.getModules();
                modules.register(new LoggingModule(executorService, logger));
                modules.register(new CommandModule(executorService, logger));
                modules.register(new CommunicationModule(executorService));
                modules.register(appControlModule);
                modules.register(appsWatchdogModule);

                stinger.start(new HashSet<>(Arrays.asList(
                        LoggingModule.class,
                        CommandModule.class,
                        CommunicationModule.class,
                        AppControlModule.class,
                        AppsWatchdogModule.class
                )));
            } finally {
                stinger.stop();
                logger.info("Stinger done");
                logger.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }
    }

    private static void loadNatives() {
        Natives.load(Natives.OPENCV_LIBNAME);
    }
}
