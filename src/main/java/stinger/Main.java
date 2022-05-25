package stinger;

import com.castle.nio.PathMatching;
import com.castle.nio.PatternPathFinder;
import com.castle.nio.temp.TempPath;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import stinger.comm.CommunicationModule;
import stinger.commands.CommandModule;
import stinger.logging.FileLogger;
import stinger.logging.LoggingModule;
import stinger.storage.PersistentStorage;
import stinger.storage.Storage;
import stinger.storage.StorageIndex;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        loadOpenCVNatives();

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

            Set<Module> customModules = new HashSet<>();
            customModules.addAll(OnStart.getCustomModules(new ModuleCreator(executorService)));

            StingerEnvironment environment = new StingerEnvironmentImpl(storage, commandModule, logger, stingerControl,
                    new StingerModuleImpl(customModules));

            Stinger stinger = new Stinger(
                    new HashSet<>(Arrays.<Module>asList(
                            commandModule,
                            new CommunicationModule(executorService),
                            new LoggingModule(executorService, logger)
                    )),
                    customModules,
                    environment);
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
            executorService.shutdownNow();
        }
    }

    private static void loadOpenCVNatives() {
        String patternStr;
        switch (com.castle.util.os.System.operatingSystem()) {
            case Windows:
                patternStr = "^.*opencv_java\\d+\\.(?:dll)$";
                break;
            case Linux:
                patternStr = "^.*opencv_java\\d+\\.(?:so)$";
                break;
            default:
                throw new AssertionError("unsupported platform");
        }
        Pattern pattern = Pattern.compile(patternStr);

        String[] classpath = java.lang.System.getProperty("java.class.path").split(":");
        for (String pathStr : classpath) {
            Path path = Paths.get(pathStr);
            if (!Files.isRegularFile(path)) {
                continue;
            }

            try {
                Zip zip = Zip.fromPath(path);
                try (OpenZip openZip = zip.open()) {
                    Path jar = openZip.find(pattern);
                    TempPath tempPath = openZip.extract(jar);

                    java.lang.System.load(tempPath.originalPath().toAbsolutePath().toString());
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // fallback, try and find path in our fallback natives folder
        try {
            Path fallbackFolder = Paths.get(System.getProperty("user.dir"), "natives");
            PatternPathFinder pathFinder = new PatternPathFinder(FileSystems.getDefault());
            Path jar = pathFinder.findOne(pattern, PathMatching.fileMatcher(), fallbackFolder);
            java.lang.System.load(jar.toAbsolutePath().toString());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new AssertionError("Unable to load opencv");
    }
}
