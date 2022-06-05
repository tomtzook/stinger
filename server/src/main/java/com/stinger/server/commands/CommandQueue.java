package com.stinger.server.commands;

import com.stinger.framework.commands.CommandDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CommandQueue {

    private final List<CommandDefinition> mCommands;

    public CommandQueue() {
        mCommands = new LinkedList<>();
    }

    public List<? extends CommandDefinition> getAllAndClear() {
        synchronized (this) {
            List<? extends CommandDefinition> commandDefinitions = new ArrayList<>(mCommands);
            mCommands.clear();
            return commandDefinitions;
        }
    }

    public void addCommand(CommandDefinition commandDefinition) {
        synchronized (this) {
            mCommands.add(commandDefinition);
        }
    }

    public void addCommands(Collection<? extends CommandDefinition> commandDefinition) {
        synchronized (this) {
            mCommands.addAll(commandDefinition);
        }
    }
}
