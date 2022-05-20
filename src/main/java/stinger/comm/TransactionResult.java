package stinger.comm;

import stinger.commands.Executable;

import java.util.List;

public class TransactionResult {

    private final List<Executable> mCommands;

    public TransactionResult(List<Executable> commands) {
        mCommands = commands;
    }

    public List<Executable> getCommands() {
        return mCommands;
    }
}
