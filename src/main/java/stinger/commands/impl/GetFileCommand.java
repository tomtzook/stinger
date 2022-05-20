package stinger.commands.impl;

import stinger.StingerEnvironment;
import stinger.commands.Command;
import stinger.storage.Storage;
import stinger.storage.impl.FileProduct;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;
import stingerlib.storage.StorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetFileCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, Parameters parameters) throws CommandException {
        try {
            String pathStr = parameters.getString("path");
            Path path = Paths.get(pathStr);

            Storage storage = environment.getStorage();
            storage.store(new FileProduct(path));
        } catch (StorageException e) {
            throw new CommandException(e);
        }
    }
}
