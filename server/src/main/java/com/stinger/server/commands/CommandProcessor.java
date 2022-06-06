package com.stinger.server.commands;

import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.stinger.framework.app.AppConf;
import com.stinger.framework.app.AppPackage;
import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.commands.CommandType;
import com.stinger.framework.commands.GenericCommandType;
import com.stinger.framework.commands.Parameters;
import com.stinger.framework.data.KnownTypes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class CommandProcessor {

    private final KnownTypes<GenericCommandType, Integer> mCommandTypes;

    public CommandProcessor(KnownTypes<GenericCommandType, Integer> commandTypes) {
        mCommandTypes = commandTypes;
    }

    public CommandDefinition processCommand(RawCommandDef def) throws IOException {
        CommandType commandType = mCommandTypes.getFromKey(def.getType());
        switch (commandType.intValue()) {
            case 20:
                processInstallAppCommand(def);
                break;
        }

        return new CommandDefinition(
                commandType,
                new Parameters(def.getParams())
        );
    }

    private void processInstallAppCommand(RawCommandDef def) throws IOException {
        Path path = Paths.get((String) def.getParams().get("path"));
        AppPackage appPackage = AppPackage.fromFile(path);
        AppConf conf = appPackage.getConf();

        def.getParams().put("id", conf.getId());
        def.getParams().put("version", conf.getVersion());

        byte[] bytes = Files.readAllBytes(path);
        def.getParams().put("codeBuffer", bytes);
    }
}
