package com.stinger.framework.commands;

import com.stinger.framework.data.TypedSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

public class ParametersSerializer extends TypedSerializer {

    public void serialize(DataOutput output, Parameters parameters) throws IOException {
        writeTypedMap(output, parameters.getAllParameters());
    }

    public Parameters deserialize(DataInput input) throws IOException {
        Map<String, Object> map = readTypedMap(input);
        return new Parameters(map);
    }
}
