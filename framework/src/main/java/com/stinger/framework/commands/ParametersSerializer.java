package com.stinger.framework.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParametersSerializer {

    public void serialize(DataOutput output, Parameters parameters) throws IOException {
        output.writeInt(parameters.count());

        for (Map.Entry<String, Object> entry : parameters) {
            output.writeUTF(entry.getKey());
            writeTypedData(output, entry.getValue());
        }
    }

    public Parameters deserialize(DataInput input) throws IOException {
        int paramCount = input.readInt();

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < paramCount; i++) {
            String key = input.readUTF();
            Object data = readTypedData(input);
            params.put(key, data);
        }

        return new Parameters(params);
    }

    private void writeTypedData(DataOutput output, Object value) throws IOException {
        if (value instanceof String) {
            output.writeInt(0);
            output.writeUTF((String) value);
        } else if (value instanceof Integer) {
            output.writeInt(1);
            output.writeInt((Integer) value);
        } else if (value instanceof Double) {
            output.writeInt(2);
            output.writeDouble((Double) value);
        } else {
            throw new IOException("unknown type: " + value.getClass());
        }
    }

    private Object readTypedData(DataInput input) throws IOException {
        int type = input.readInt();
        switch (type) {
            case 0:
                return input.readUTF();
            case 1:
                return input.readInt();
            case 2:
                return input.readDouble();
            default:
                throw new IOException("unknown type: " + type);
        }
    }
}
