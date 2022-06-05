package com.stinger.server;

import com.stinger.server.comm.CommunicationModule;
import com.stinger.server.comm.Communicator;
import com.stinger.server.commands.CommandQueue;
import com.stinger.server.commands.CommandsModule;
import com.stinger.server.commands.GenericCommandType;
import com.stinger.server.storage.GenericProductType;
import com.stinger.server.storage.Storage;
import com.stinger.server.util.GenericTypesParser;
import com.stinger.server.util.KnownTypes;
import com.stinger.framework.logging.BasicFileLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newScheduledThreadPool(3);
        try {
            ServerFiles files = new ServerFiles();
            BasicFileLogger logger = new BasicFileLogger(files.getLogFile());

            KnownTypes<GenericCommandType, Integer> knownCommandTypes =
                    new GenericTypesParser<GenericCommandType, Integer>(GenericCommandType.class)
                        .parseFromFile(files.getCommandTypesFile());
            KnownTypes<GenericProductType, Integer> knownProductTypes =
                    new GenericTypesParser<GenericProductType, Integer>(GenericProductType.class)
                            .parseFromFile(files.getProductTypesFile());

            CommandQueue commandQueue = new CommandQueue();
            Storage storage = new Storage(files.getStorageRoot(), logger);

            CommunicationModule communicationModule = new CommunicationModule(
                    executorService, new Communicator(Constants.COMMUNICATION_BIND_ADDRESS));
            CommandsModule commandsModule = new CommandsModule(executorService, files.getCommandsDirectory());

            Environment environment = new Environment(knownCommandTypes, knownProductTypes,
                    commandQueue, storage, logger);

            try {
                logger.info("Starting server");

                communicationModule.start(environment);
                commandsModule.start(environment);

                logger.info("Running");

                while (true) {
                    Thread.sleep(1000);
                }
            } finally {
                logger.info("Stopping server");
                communicationModule.stop();
                commandsModule.stop();

                logger.info("Done");
                logger.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }
    }
}
