package stinger.commands.impl;

import stinger.StingerEnvironment;
import stinger.commands.Command;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;

public class StopKeyloggerCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, Parameters parameters) throws CommandException {
        environment.getModules().getKeylogger().stop(environment);
    }
}
