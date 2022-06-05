package com.stinger.framework.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpSocketConnection implements StreamConnection {

    private final Socket mSocket;

    public TcpSocketConnection(Socket socket) {
        mSocket = socket;
    }

    @Override
    public InputStream inputStream() throws IOException {
        return mSocket.getInputStream();
    }

    @Override
    public OutputStream outputStream() throws IOException {
        return mSocket.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        mSocket.close();
    }
}
