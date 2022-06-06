package com.stinger.server.commands;

import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.commands.CommandType;
import com.stinger.framework.commands.GenericCommandType;
import com.stinger.framework.commands.Parameters;
import com.stinger.framework.data.KnownTypes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandProcessor {

    private final KnownTypes<GenericCommandType, Integer> mCommandTypes;

    public CommandProcessor(KnownTypes<GenericCommandType, Integer> commandTypes) {
        mCommandTypes = commandTypes;
    }

    public CommandDefinition processCommand(RawCommandDef def) throws IOException {
        CommandType commandType = mCommandTypes.getFromKey(def.getType());
        switch (commandType.intValue()) {
            case 20: {
                Path path = Paths.get((String) def.getParams().get("path"));
                byte[] bytes = Files.readAllBytes(path);
                def.getParams().put("codeBuffer", bytes);
                break;
            }
        }

        return new CommandDefinition(
                commandType,
                new Parameters(def.getParams())
        );
    }
}
