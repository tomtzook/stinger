package stinger.comm;

import stinger.commands.Executable;
import stinger.commands.StCommandDefinition;

import java.util.List;

public class TransactionResult {

    private final List<StCommandDefinition> mCommands;

    public TransactionResult(List<StCommandDefinition> commands) {
        mCommands = commands;
    }

    public List<StCommandDefinition> getCommands() {
        return mCommands;
    }
}
