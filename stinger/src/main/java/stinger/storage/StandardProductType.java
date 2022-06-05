package stinger.storage;

import com.stinger.framework.storage.ProductType;

public enum StandardProductType implements ProductType {
    FILE(1),
    BLOB(2),
    LOG(3),
    LIST_DIR(4),
    SCREENSHOT(5),
    KEY_LOGGER(6)
    ;

    private final int mIntValue;

    StandardProductType(int intValue) {
        mIntValue = intValue;
    }

    @Override
    public int intValue() {
        return mIntValue;
    }

    public static StandardProductType fromInt(int value) {
        for (StandardProductType type : values()) {
            if (type.intValue() == value) {
                return type;
            }
        }

        throw new EnumConstantNotPresentException(StandardProductType.class,
                String.valueOf(value));
    }
}
