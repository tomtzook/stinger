package stinger.commands;

import com.stinger.framework.commands.Parameters;

import java.util.Collection;

public interface CommandQueue {

    void addCommand(Command command, Parameters parameters);
    void addCommandDefinitions(Collection<? extends StCommandDefinition> definition);
}
