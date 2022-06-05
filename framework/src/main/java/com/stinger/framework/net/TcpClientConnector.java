package com.stinger.framework.net;

import com.stinger.framework.util.ThrowingSupplier;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpClientConnector implements Connector<StreamConnection> {

    private final ThrowingSupplier<? extends Socket, ? extends IOException> mSocketCreator;
    private final SocketAddress mEndPoint;

    public TcpClientConnector(ThrowingSupplier<? extends Socket, ? extends IOException> socketCreator,
                              SocketAddress endPoint) {
        mSocketCreator = socketCreator;
        mEndPoint = endPoint;
    }

    public TcpClientConnector(SocketAddress endPoint, int readTimeoutMs) {
        this(() -> {
            Socket socket = new Socket();
            socket.setSoTimeout(readTimeoutMs);
            return socket;
        }, endPoint);
    }

    @Override
    public StreamConnection connect(long timeoutMs) throws IOException {
        Socket socket = mSocketCreator.get();
        try {
            socket.connect(mEndPoint, (int) timeoutMs);
            return new TcpSocketConnection(socket);
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
                e.addSuppressed(e1);
            }

            throw e;
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String toString() {
        return "TcpClientConnector{" +
                "mEndPoint=" + mEndPoint +
                '}';
    }
}
