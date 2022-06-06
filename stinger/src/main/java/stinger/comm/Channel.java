package stinger.comm;

import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.commands.CommandSerializer;
import com.stinger.framework.commands.CommandType;
import com.stinger.framework.commands.ParametersSerializer;
import com.stinger.framework.data.TypedSerializer;
import com.stinger.framework.net.MessageType;
import com.stinger.framework.net.StreamConnection;
import com.stinger.framework.storage.ProductSerializer;
import com.stinger.framework.storage.StoredProduct;
import stinger.commands.StCommandDefinition;
import stinger.commands.StandardCommandType;
import stinger.meta.ToolMeta;
import stinger.storage.StandardProductType;

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
    private final TypedSerializer mSerializer;

    public Channel(StreamConnection connection) throws IOException {
        mConnection = connection;
        mOutput = new DataOutputStream(mConnection.outputStream());
        mInput = new DataInputStream(mConnection.inputStream());

        mCommandSerializer = new CommandSerializer(StandardCommandType::fromInt, new ParametersSerializer());
        mProductSerializer = new ProductSerializer(StandardProductType::fromInt);
        mSerializer = new TypedSerializer();
    }

    public void sendToolMeta(String toolId, ToolMeta meta) throws IOException {
        mOutput.writeInt(MessageType.META.intValue());
        mOutput.writeUTF(toolId);
        mSerializer.writeTypedMap(mOutput, meta.asMap());
    }

    public void sendProduct(String toolId, StoredProduct product) throws IOException {
        mOutput.writeInt(MessageType.NEW_PRODUCT.intValue());
        mOutput.writeUTF(toolId);
        mProductSerializer.serialize(mOutput, product);
    }

    public List<StCommandDefinition> readCommands(String toolId) throws IOException {
        mOutput.writeInt(MessageType.REQUEST_COMMANDS.intValue());
        mOutput.writeUTF(toolId);

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
