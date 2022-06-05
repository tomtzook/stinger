package stinger;

import stinger.comm.CommunicationModule;
import stinger.commands.CommandModule;
import stinger.logging.FileLogger;
import stinger.logging.LoggingModule;
import stinger.modules.Module;
import stinger.modules.StingerModuleImpl;
import stinger.modules.StingerModules;
import stinger.storage.PersistentStorage;
import stinger.storage.Storage;
import stinger.storage.StorageIndex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        loadNatives();

        ExecutorService executorService = Executors.newFixedThreadPool(Constants.CORE_THREAD_POOL_SIZE);
        try {
            StingerFiles files = new StingerFiles();

            StingerControl stingerControl = new StringerControlImpl(Thread.currentThread());
            FileLogger logger = new FileLogger(files.getLogFile());
            Storage storage = new PersistentStorage(
                    files.getStorageRoot(),
                    StorageIndex.inFile(files.getStorageIndexDbPath(), logger)
            );

            StingerEnvironmentImpl environment = new StingerEnvironmentImpl(
                    executorService,
                    storage,
                    logger,
                    stingerControl);

            Stinger stinger = new Stinger(environment);
            try {
                logger.info("Stinger start");

                StingerModules modules = environment.getModules();
                modules.register(new LoggingModule(executorService, logger));
                modules.register(new CommandModule(executorService, logger));
                modules.register(new CommunicationModule(executorService));

                stinger.start(new HashSet<>(Arrays.asList(
                        LoggingModule.class,
                        CommandModule.class,
                        CommunicationModule.class
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
