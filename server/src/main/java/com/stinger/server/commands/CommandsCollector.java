package com.stinger.server.commands;

import com.google.gson.Gson;
import com.stinger.server.util.KnownTypes;
import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.commands.CommandType;
import com.stinger.framework.commands.Parameters;
import com.stinger.framework.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandsCollector {

    private final Path mCommandsDir;
    private final CommandQueue mCommandQueue;
    private final KnownTypes<GenericCommandType, Integer> mCommandTypes;
    private final Logger mLogger;
    private final Gson mGson;

    public CommandsCollector(Path commandsDir, CommandQueue commandQueue,
                             KnownTypes<GenericCommandType, Integer> commandTypes, Logger logger) {
        mCommandsDir = commandsDir;
        mCommandQueue = commandQueue;
        mCommandTypes = commandTypes;
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
            CommandType commandType = mCommandTypes.getFromKey(def.getType());
            CommandDefinition commandDefinition = new CommandDefinition(
                    commandType,
                    new Parameters(def.getParams())
            );
            commandDefinitions.add(commandDefinition);
        }

        return commandDefinitions;
    }

    private static class RawCommandDef {

        private final int mType;
        private final Map<String, Object> mParams;

        private RawCommandDef(int type, Map<String, Object> params) {
            mType = type;
            mParams = params;
        }

        public int getType() {
            return mType;
        }

        public Map<String, Object> getParams() {
            return mParams;
        }
    }
}
