package com.stinger.server.comm;

import com.stinger.framework.commands.GenericCommandType;
import com.stinger.framework.data.KnownTypes;
import com.stinger.framework.net.StreamConnection;
import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.commands.CommandSerializer;
import com.stinger.framework.commands.ParametersSerializer;
import com.stinger.framework.logging.Logger;
import com.stinger.framework.net.MessageType;
import com.stinger.framework.storage.GenericProductType;
import com.stinger.framework.storage.ProductSerializer;
import com.stinger.framework.storage.StoredProduct;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ServerChannel implements Closeable {

    private final StreamConnection mConnection;
    private final Supplier<List<? extends CommandDefinition>> mCommandsSupplier;
    private final Consumer<StoredProduct> mProductConsumer;
    private final Logger mLogger;

    private final DataOutput mOutput;
    private final DataInput mInput;

    private final CommandSerializer mCommandSerializer;
    private final ProductSerializer mProductSerializer;

    public ServerChannel(StreamConnection connection,
                         Supplier<List<? extends CommandDefinition>> commandsSupplier,
                         Consumer<StoredProduct> productConsumer,
                         KnownTypes<GenericCommandType, Integer> commandTypes,
                         KnownTypes<GenericProductType, Integer> productTypes,
                         Logger logger) throws IOException {
        mConnection = connection;
        mCommandsSupplier = commandsSupplier;
        mProductConsumer = productConsumer;
        mLogger = logger;

        mOutput = new DataOutputStream(mConnection.outputStream());
        mInput = new DataInputStream(mConnection.inputStream());

        mCommandSerializer = new CommandSerializer(commandTypes::getFromKey, new ParametersSerializer());
        mProductSerializer = new ProductSerializer(productTypes::getFromKey);
    }

    public boolean handleNextRequest() throws IOException {
        int requestInt = mInput.readInt();
        MessageType messageType = MessageType.fromInt(requestInt);

        switch (messageType) {
            case NEW_PRODUCT:
                mProductConsumer.accept(readProduct());
                break;
            case REQUEST_COMMANDS:
                List<? extends CommandDefinition> commandDefinitions = mCommandsSupplier.get();
                sendCommands(commandDefinitions);
                break;
            case DONE:
                return false;
        }

        return true;
    }

    private void sendCommands(List<? extends CommandDefinition> commandDefinitions) throws IOException {
        mOutput.writeInt(commandDefinitions.size());

        for (CommandDefinition commandDefinition : commandDefinitions) {
            mLogger.info("Sending command %s", commandDefinition);
            mCommandSerializer.serialize(mOutput, commandDefinition);
        }
    }

    private StoredProduct readProduct() throws IOException {
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
