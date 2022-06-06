package com.stinger.server.comm;

import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.commands.CommandSerializer;
import com.stinger.framework.commands.GenericCommandType;
import com.stinger.framework.commands.ParametersSerializer;
import com.stinger.framework.data.KnownTypes;
import com.stinger.framework.data.TypedSerializer;
import com.stinger.framework.logging.Logger;
import com.stinger.framework.net.MessageType;
import com.stinger.framework.net.StreamConnection;
import com.stinger.framework.storage.GenericProductType;
import com.stinger.framework.storage.ProductSerializer;
import com.stinger.framework.storage.StoredProduct;
import com.stinger.server.storage.ToolStorage;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ServerChannel implements Closeable {

    private final StreamConnection mConnection;
    private final Logger mLogger;

    private final DataOutput mOutput;
    private final DataInput mInput;

    private final CommandSerializer mCommandSerializer;
    private final ProductSerializer mProductSerializer;
    private final TypedSerializer mSerializer;

    public ServerChannel(StreamConnection connection,
                         KnownTypes<GenericCommandType, Integer> commandTypes,
                         KnownTypes<GenericProductType, Integer> productTypes,
                         Logger logger) throws IOException {
        mConnection = connection;
        mLogger = logger;

        mOutput = new DataOutputStream(mConnection.outputStream());
        mInput = new DataInputStream(mConnection.inputStream());

        mCommandSerializer = new CommandSerializer(commandTypes::getFromKey, new ParametersSerializer());
        mProductSerializer = new ProductSerializer(productTypes::getFromKey);
        mSerializer = new TypedSerializer();
    }

    public MessageType readType() throws IOException {
        int requestInt = mInput.readInt();
        return MessageType.fromInt(requestInt);
    }

    public String readToolId() throws IOException {
        return mInput.readUTF();
    }

    public Map<String, Object> readToolMeta() throws IOException {
        return mSerializer.readTypedMap(mInput);
    }

    public void sendCommands(List<? extends CommandDefinition> commandDefinitions) throws IOException {
        mOutput.writeInt(commandDefinitions.size());

        for (CommandDefinition commandDefinition : commandDefinitions) {
            mLogger.info("Sending command %s", commandDefinition);
            mCommandSerializer.serialize(mOutput, commandDefinition);
        }
    }

    public StoredProduct readProduct() throws IOException {
        StoredProduct product = mProductSerializer.deserialize(mInput);
        mLogger.info("Received product %s (%s)",
                product.getMetadata().getId(),
                product.getMetadata().getType());
        return product;
    }

    @Override
    public void close() throws IOException {
        mConnection.close();
    }
}
