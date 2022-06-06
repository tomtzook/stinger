package com.stinger.server.commands;

import com.google.gson.Gson;
import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandsCollector {

    private final Path mCommandsDir;
    private final CommandQueue mCommandQueue;
    private final CommandProcessor mCommandProcessor;
    private final Logger mLogger;
    private final Gson mGson;

    public CommandsCollector(Path commandsDir, CommandQueue commandQueue,
                             CommandProcessor commandProcessor,
                             Logger logger) {
        mCommandsDir = commandsDir;
        mCommandQueue = commandQueue;
        mCommandProcessor = commandProcessor;
        mLogger = logger;
        mGson = new Gson();
    }

    public void collectAll() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(mCommandsDir)) {
            for (Path path : stream) {
                try {
                    mLogger.info("Collecting from %s", path.toString());
                    collect(path);
                } catch (Throwable t) {
                    mLogger.error("Error with file", path.toAbsolutePath().toString());
                    mLogger.error("stacktrace", t);
                }
            }
        }
    }

    private void collect(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            RawCommandDef[] commandDefinitions = mGson.fromJson(reader, RawCommandDef[].class);
            List<CommandDefinition> commandsList = parseCommands(commandDefinitions);
            mLogger.info("Collected new commands: %s", commandsList);
            mCommandQueue.addCommands(commandsList);
        } finally {
            try {
                Files.delete(path);
            } catch (IOException e1) {}
        }
    }

    private List<CommandDefinition> parseCommands(RawCommandDef[] commandDefs) {
        List<CommandDefinition> commandDefinitions = new ArrayList<>(commandDefs.length);
        for (RawCommandDef def : commandDefs) {
            try {
                CommandDefinition commandDefinition = mCommandProcessor.processCommand(def);
                commandDefinitions.add(commandDefinition);
            } catch (Throwable t) {
                mLogger.error("Error processing command definition", t);
            }
        }

        return commandDefinitions;
    }
}
