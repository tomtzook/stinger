package stinger.commands.impl;

import stinger.StingerEnvironment;
import stinger.commands.Command;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;

public class ListDirCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, Parameters parameters) throws CommandException {
        // TODO: IMPLEMENT
        // This command should receive directory information in the parameters.
        // and list all the files in the directory until a certain depth.
        // If the directory doesn't exist, log.
        // If the path is not a directory, log.
        // parameters:
        // - path: path to the directory to list.
        // - depth: depth of subdirectories to go into.
        //          1 = only the files under the given directory
        //          2 = also the files under subdirectories.
        //          so on....
        // result:
        // - product containing information about the list. Use BinaryProduct with a string.
        //      Remember to pass StandardProductType.LIST_DIR to it.
    }
}
