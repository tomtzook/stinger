package stinger.commands;

import stingerlib.commands.Parameters;

import java.util.Collection;

public interface CommandQueue {

    void addCommand(Command command, Parameters parameters);
    void addCommandDefinitions(Collection<? extends StCommandDefinition> definition);
}
