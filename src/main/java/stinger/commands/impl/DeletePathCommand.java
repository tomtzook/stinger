package stinger.commands.impl;

import stinger.StingerEnvironment;
import stinger.commands.Command;
import stinger.storage.impl.BinaryProduct;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;
import stingerlib.storage.StorageException;

public class DeletePathCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, Parameters parameters) throws CommandException {
        // TODO: IMPLEMENT
        // This command should receive file information in the parameters.
        // and delete the file in the path.
        // If the file doesn't exist, do nothing.
        // If path is a file, delete file. If path is a directory, delete directory.
        // parameters:
        // - path: path to the file to delete
    }
}
