package stinger.commands;

import com.stinger.framework.commands.Parameters;

public class StCommandDefinition {

    private final StandardCommandType mType;
    private final Parameters mParameters;

    public StCommandDefinition(StandardCommandType type, Parameters parameters) {
        mType = type;
        mParameters = parameters;
    }

    public StandardCommandType getType() {
        return mType;
    }

    public Parameters getParameters() {
        return mParameters;
    }
}
