package com.stinger.framework.commands;

public class CommandDefinition {

    private final CommandType mType;
    private final Parameters mParameters;

    public CommandDefinition(CommandType type, Parameters parameters) {
        mType = type;
        mParameters = parameters;
    }

    public CommandType getType() {
        return mType;
    }

    public Parameters getParameters() {
        return mParameters;
    }

    @Override
    public String toString() {
        return "CommandDefinition{" +
                "mType=" + mType +
                ", mParameters=" + mParameters +
                '}';
    }
}
