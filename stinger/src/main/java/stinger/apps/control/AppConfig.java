package stinger.apps.control;

public class AppConfig {

    private final int mId;
    private final String mVersion;

    public AppConfig(int id, String version) {
        mId = id;
        mVersion = version;
    }

    public int getId() {
        return mId;
    }

    public String getVersion() {
        return mVersion;
    }
}
