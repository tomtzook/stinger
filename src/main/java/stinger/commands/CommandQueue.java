package stinger.commands;

import java.util.Collection;

public interface CommandQueue {

    void addCommand(Executable executable);
    void addCommands(Collection<? extends Executable> executables);
}
