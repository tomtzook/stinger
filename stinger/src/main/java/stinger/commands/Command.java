package stinger.commands;

import stinger.StingerEnvironment;
import com.stinger.framework.commands.CommandException;
import com.stinger.framework.commands.Parameters;

public interface Command {

    void execute(StingerEnvironment environment, CommandConfig config, Parameters parameters)
            throws CommandException;
}
