package com.stinger.framework.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.Function;

public class CommandSerializer {

    private final Function<Integer, CommandType> mCommandClassifier;
    private final ParametersSerializer mParametersSerializer;

    public CommandSerializer(Function<Integer, CommandType> commandClassifier, ParametersSerializer parametersSerializer) {
        mCommandClassifier = commandClassifier;
        mParametersSerializer = parametersSerializer;
    }

    public void serialize(DataOutput output, CommandDefinition commandDefinition) throws IOException {
        output.writeInt(commandDefinition.getType().intValue());
        mParametersSerializer.serialize(output, commandDefinition.getParameters());
    }

    public CommandDefinition deserialize(DataInput input) throws IOException {
        int typeInt = input.readInt();
        CommandType type = mCommandClassifier.apply(typeInt);

        Parameters parameters = mParametersSerializer.deserialize(input);
        return new CommandDefinition(type, parameters);
    }
}
