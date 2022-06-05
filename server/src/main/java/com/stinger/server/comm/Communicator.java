package com.stinger.server.comm;

import com.castle.net.Connector;
import com.castle.net.StreamConnection;
import com.castle.net.tcp.TcpServerConnector;
import com.castle.time.Time;
import com.castle.time.exceptions.TimeoutException;
import com.stinger.server.Environment;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;

public class Communicator implements Closeable {

    private final Connector<StreamConnection> mConnector;

    public Communicator(Connector<StreamConnection> connector) {
        mConnector = connector;
    }

    public Communicator(SocketAddress bindAddress) throws IOException {
        this(new TcpServerConnector(bindAddress, 100));
    }

    public void handleNextClient(Environment environment) throws IOException, TimeoutException {
        environment.getLogger().info("Waiting connect %s", mConnector);
        StreamConnection connection = mConnector.connect(Time.milliseconds(1000));
        doTransaction(environment, connection);
    }

    @Override
    public void close() throws IOException {
        mConnector.close();
    }

    private void doTransaction(Environment environment, StreamConnection connection) throws IOException {
        environment.getLogger().info("Doing transaction");
        try (ServerChannel channel = openChannel(environment, connection)) {
            while (channel.handleNextRequest());
        } finally {
            environment.getLogger().info("Transaction done");
        }
    }

    private ServerChannel openChannel(Environment environment, StreamConnection connection) throws IOException {
        return new ServerChannel(connection,
                environment.getCommandQueue()::getAllAndClear,
                environment.getStorage()::save,
                environment.getCommandTypes(),
                environment.getProductTypes(),
                environment.getLogger());
    }
}
