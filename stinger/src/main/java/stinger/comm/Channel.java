package stinger.comm;

import stinger.commands.StCommandDefinition;
import stinger.commands.StandardCommandType;
import stinger.storage.StandardProductType;
import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.commands.CommandSerializer;
import com.stinger.framework.commands.CommandType;
import com.stinger.framework.commands.ParametersSerializer;
import com.stinger.framework.net.MessageType;
import com.stinger.framework.net.StreamConnection;
import com.stinger.framework.storage.ProductSerializer;
import com.stinger.framework.storage.StoredProduct;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Channel implements Closeable {

    private final StreamConnection mConnection;
    private final DataOutput mOutput;
    private final DataInput mInput;

    private final CommandSerializer mCommandSerializer;
    private final ProductSerializer mProductSerializer;

    public Channel(StreamConnection connection) throws IOException {
        mConnection = connection;
        mOutput = new DataOutputStream(mConnection.outputStream());
        mInput = new DataInputStream(mConnection.inputStream());

        mCommandSerializer = new CommandSerializer(StandardCommandType::fromInt, new ParametersSerializer());
        mProductSerializer = new ProductSerializer(StandardProductType::fromInt);
    }

    public void sendProduct(StoredProduct product) throws IOException {
        mOutput.writeInt(MessageType.NEW_PRODUCT.intValue());
        mProductSerializer.serialize(mOutput, product);
    }

    public List<StCommandDefinition> readCommands() throws IOException {
        mOutput.writeInt(MessageType.REQUEST_COMMANDS.intValue());
        int commandCount = mInput.readInt();
        List<StCommandDefinition> commands = new ArrayList<>();
        for (int i = 0; i < commandCount; i++) {
            StCommandDefinition executable = readCommand();
            commands.add(executable);
        }

        return commands;
    }

    private StCommandDefinition readCommand() throws IOException {
        CommandDefinition commandDefinition = mCommandSerializer.deserialize(mInput);
        CommandType commandType = commandDefinition.getType();
        if (!(commandType instanceof StandardCommandType)) {
            throw new IOException("unknown command type");
        }

        return new StCommandDefinition((StandardCommandType) commandType, commandDefinition.getParameters());
    }

    @Override
    public void close() throws IOException {
        mOutput.writeInt(MessageType.DONE.intValue());
        mConnection.close();
    }
}
