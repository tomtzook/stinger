package stinger.commands;

import stinger.StingerEnvironment;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;

public interface Command {

    void execute(StingerEnvironment environment, CommandConfig config, Parameters parameters)
            throws CommandException;
}
