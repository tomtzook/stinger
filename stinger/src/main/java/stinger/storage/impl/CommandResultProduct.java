package stinger.storage.impl;

import com.stinger.framework.data.TypedJsonSerializer;
import com.stinger.framework.storage.Product;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CommandResultProduct implements Product {

    private final String mCommandId;
    private final Throwable mError;
    private final boolean mDidFinishSuccessfully;

    public CommandResultProduct(String commandId) {
        mCommandId = commandId;
        mError = null;
        mDidFinishSuccessfully = true;
    }

    public CommandResultProduct(String commandId, Throwable error) {
        mCommandId = commandId;
        mError = error;
        mDidFinishSuccessfully = false;
    }

    @Override
    public InputStream open() throws IOException {
        Map<String, Object> data = createDataMap();
        TypedJsonSerializer serializer = new TypedJsonSerializer();
        String string = serializer.writeTypedMap(data).toString();

        return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> createDataMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("commandId", mCommandId);
        data.put("success", mDidFinishSuccessfully);

        if (mError != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            mError.printStackTrace(pw);
            data.put("error", sw.toString());
        }

        return data;
    }
}
