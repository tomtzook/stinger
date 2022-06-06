package com.stinger.framework.db;

import java.io.Closeable;
import java.io.IOException;

public interface Connection extends Closeable {

    Transaction openTransaction() throws IOException;
}
