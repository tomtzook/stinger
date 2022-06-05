package stinger.commands.impl;

import stinger.StingerEnvironment;
import stinger.commands.Command;
import stinger.commands.CommandConfig;
import stinger.storage.Storage;
import stinger.storage.impl.FileProduct;
import com.stinger.framework.commands.CommandException;
import com.stinger.framework.commands.Parameters;
import com.stinger.framework.storage.StorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetFileCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, CommandConfig config, Parameters parameters) throws CommandException {
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
