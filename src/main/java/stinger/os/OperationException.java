package stinger.os;

import stingerlib.commands.CommandException;

public class OperationException extends CommandException {

    public OperationException(String message) {
        super(message);
    }

    public OperationException(Throwable cause) {
        super(cause);
    }
}
