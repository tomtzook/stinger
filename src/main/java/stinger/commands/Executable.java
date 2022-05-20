package stinger.commands;

import stinger.StingerEnvironment;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;

public class Executable {

    private final Command mCommand;
    private final Parameters mParameters;

    public Executable(Command command, Parameters parameters) {
        mCommand = command;
        mParameters = parameters;
    }

    public void execute(StingerEnvironment environment) throws CommandException {
        mCommand.execute(environment, mParameters);
    }

    @Override
    public String toString() {
        return "Executable{" +
                "mCommand=" + mCommand.getClass().getName() +
                ", mParameters=" + mParameters +
                '}';
    }
}
