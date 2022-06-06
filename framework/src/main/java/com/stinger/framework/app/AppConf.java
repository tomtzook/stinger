package com.stinger.framework.app;

public class AppConf {

    private final int mId;
    private final String mVersion;

    public AppConf(int id, String version) {
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
