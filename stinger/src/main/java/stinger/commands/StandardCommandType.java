package stinger.commands;

import stinger.commands.impl.GetFileCommand;
import stinger.commands.impl.InstallAppCommand;
import stinger.commands.impl.StopCommand;
import com.stinger.framework.commands.CommandType;
import stinger.commands.impl.UninstallAppCommand;

public enum StandardCommandType implements CommandType {
    GET_FILE(2) {
        @Override
        public Command createCommand() {
            return new GetFileCommand();
        }
    },
    STOP(3) {
        @Override
        public Command createCommand() {
            return new StopCommand();
        }
    },
    INSTALL_APP(20) {
        @Override
        public Command createCommand() {
            return new InstallAppCommand();
        }
    },
    UNINSTALL_APP(21) {
        @Override
        public Command createCommand() {
            return new UninstallAppCommand();
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
