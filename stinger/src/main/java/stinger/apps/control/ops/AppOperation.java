package stinger.apps.control.ops;

public class AppOperation {

    private final int mId;
    private final OperationType mType;

    public AppOperation(int id, OperationType type) {
        mId = id;
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public OperationType getType() {
        return mType;
    }
}
