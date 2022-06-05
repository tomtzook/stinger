package com.stinger.framework.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpServerConnector implements Connector<StreamConnection> {

    private final ServerSocket mServerSocket;

    public TcpServerConnector(ServerSocket serverSocket) {
        mServerSocket = serverSocket;
    }

    public TcpServerConnector(SocketAddress bindAddress) throws IOException {
        mServerSocket = new ServerSocket();
        mServerSocket.bind(bindAddress);
    }

    @Override
    public StreamConnection connect(long timeoutMs) throws IOException {
        Socket socket = mServerSocket.accept();
        try {
            socket.setSoTimeout((int) timeoutMs);
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
        mServerSocket.close();
    }

    @Override
    public String toString() {
        return "TcpServerConnector{" +
                "mServerSocket=" + mServerSocket +
                '}';
    }
}
