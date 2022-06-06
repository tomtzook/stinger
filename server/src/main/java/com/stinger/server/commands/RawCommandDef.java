package com.stinger.server.commands;

import java.util.Map;

public class RawCommandDef {

    private final int mType;
    private final Map<String, Object> mParams;

    public RawCommandDef(int type, Map<String, Object> params) {
        mType = type;
        mParams = params;
    }

    public int getType() {
        return mType;
    }

    public Map<String, Object> getParams() {
        return mParams;
    }
}
