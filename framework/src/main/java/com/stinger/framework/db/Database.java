package com.stinger.framework.db;

import java.io.Closeable;
import java.io.IOException;

public interface Database extends Closeable {

    Connection open() throws IOException;
}
