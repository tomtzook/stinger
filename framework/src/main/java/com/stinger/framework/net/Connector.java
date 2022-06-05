package com.stinger.framework.net;

import java.io.Closeable;
import java.io.IOException;

public interface Connector<T extends Connection> extends Closeable {

    T connect(long timeoutMs) throws IOException;

    @Override
    void close() throws IOException;
}
