package com.stinger.framework.net;

public enum MessageType {
    DONE(1),
    META(2),
    REQUEST_COMMANDS(5),
    NEW_PRODUCT(10);

    private final int mIntValue;

    MessageType(int intValue) {
        mIntValue = intValue;
    }

    public int intValue() {
        return mIntValue;
    }

    public static MessageType fromInt(int value) {
        for (MessageType type : values()) {
            if (type.intValue() == value) {
                return type;
            }
        }

        throw new EnumConstantNotPresentException(MessageType.class,
                String.valueOf(value));
    }
}
