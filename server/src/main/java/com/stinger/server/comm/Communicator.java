package com.stinger.server.comm;

import com.stinger.framework.commands.CommandDefinition;
import com.stinger.framework.net.Connector;
import com.stinger.framework.net.MessageType;
import com.stinger.framework.net.StreamConnection;
import com.stinger.framework.net.TcpServerConnector;
import com.stinger.framework.storage.StoredProduct;
import com.stinger.server.Environment;
import com.stinger.server.storage.ToolStorage;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public class Communicator implements Closeable {

    private final Connector<StreamConnection> mConnector;

    public Communicator(Connector<StreamConnection> connector) {
        mConnector = connector;
    }

    public Communicator(SocketAddress bindAddress) throws IOException {
        this(new TcpServerConnector(bindAddress));
    }

    public void handleNextClient(Environment environment) throws IOException {
        environment.getLogger().info("Waiting connect %s", mConnector);
        StreamConnection connection = mConnector.connect(1000);
        doTransaction(environment, connection);
    }

    @Override
    public void close() throws IOException {
        mConnector.close();
    }

    private void doTransaction(Environment environment, StreamConnection connection) throws IOException {
        environment.getLogger().info("Doing transaction");
        try (ServerChannel channel = openChannel(environment, connection)) {
            while(handleNextRequest(environment, channel));
        } finally {
            environment.getLogger().info("Transaction done");
        }
    }

    private ServerChannel openChannel(Environment environment, StreamConnection connection) throws IOException {
        return new ServerChannel(connection,
                environment.getCommandTypes(),
                environment.getProductTypes(),
                environment.getLogger());
    }

    public boolean handleNextRequest(Environment environment, ServerChannel channel) throws IOException {
        MessageType messageType = channel.readType();
        if (messageType == MessageType.DONE) {
            return false;
        }

        String toolId = channel.readToolId();

        switch (messageType) {
            case NEW_PRODUCT:
                StoredProduct product = channel.readProduct();
                environment.getStorage().getStorageForTool(toolId).save(product);
                break;
            case REQUEST_COMMANDS:
                List<? extends CommandDefinition> commandDefinitions = environment.getCommandQueue().getAllAndClear();
                channel.sendCommands(commandDefinitions);
                break;
            case META:
                Map<String, Object> meta = channel.readToolMeta();
                environment.getStorage().getStorageForTool(toolId).storeToolMeta(meta);
                break;
        }

        return true;
    }
}
