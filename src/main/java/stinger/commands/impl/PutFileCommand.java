package stinger.commands.impl;

import stinger.StingerEnvironment;
import stinger.commands.Command;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class PutFileCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, Parameters parameters) throws CommandException {
        // TODO: IMPLEMENT
        // This command should receive file information in the parameters.
        // and place the file in the path with the content.
        // If path is already taken by a file, log.
        // parameters:
        // - path: path to place the file in
        // - content: content of the file to place
    }
}
