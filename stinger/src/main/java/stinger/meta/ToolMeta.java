package stinger.meta;

import java.util.HashMap;
import java.util.Map;

public class ToolMeta {

    private final String mId;
    private final String mVersion;

    public ToolMeta(String id, String version) {
        mId = id;
        mVersion = version;
    }

    public String getId() {
        return mId;
    }

    public String getVersion() {
        return mVersion;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", mId);
        map.put("version", mVersion);

        return map;
    }
}
