package stinger.comm;

import stinger.commands.Command;
import stinger.commands.Executable;
import stinger.commands.StandardCommandType;
import stinger.storage.StandardProductType;
import stingerlib.commands.CommandDefinition;
import stingerlib.commands.CommandSerializer;
import stingerlib.commands.CommandType;
import stingerlib.commands.ParametersSerializer;
import stingerlib.net.MessageType;
import stingerlib.net.StreamConnection;
import stingerlib.storage.ProductSerializer;
import stingerlib.storage.StoredProduct;

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

    public List<Executable> readCommands() throws IOException {
        mOutput.writeInt(MessageType.REQUEST_COMMANDS.intValue());
        int commandCount = mInput.readInt();
        List<Executable> commands = new ArrayList<>();
        for (int i = 0; i < commandCount; i++) {
            Executable executable = readCommand();
            commands.add(executable);
        }

        return commands;
    }

    private Executable readCommand() throws IOException {
        CommandDefinition commandDefinition = mCommandSerializer.deserialize(mInput);
        CommandType commandType = commandDefinition.getType();
        if (!(commandType instanceof StandardCommandType)) {
            throw new IOException("unknown command type");
        }

        Command command = ((StandardCommandType)commandType).createCommand();
        return new Executable(command, commandDefinition.getParameters());
    }

    @Override
    public void close() throws IOException {
        mOutput.writeInt(MessageType.DONE.intValue());
        mConnection.close();
    }
}
