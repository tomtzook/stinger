package stinger.commands.impl;

import stinger.StingerEnvironment;
import stinger.commands.Command;
import stinger.commands.CommandConfig;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;

public class StopCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, CommandConfig config, Parameters parameters) throws CommandException {
        environment.getLogger().info("Shutting Down...");
        environment.getControl().startShutdown();
    }
}
