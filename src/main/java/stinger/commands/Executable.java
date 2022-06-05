package stinger.commands;

import stinger.StingerEnvironment;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;

public class Executable {

    private final Command mCommand;
    private final CommandConfig mConfig;
    private final Parameters mParameters;

    public Executable(Command command, CommandConfig config, Parameters parameters) {
        mCommand = command;
        mConfig = config;
        mParameters = parameters;
    }

    public void execute(StingerEnvironment environment) throws CommandException {
        mCommand.execute(environment, mConfig, mParameters);
    }
}
