package stinger.logging;

import stingerlib.storage.Product;

import java.io.IOException;

public interface LoggerControl {

    long getRecordCount();
    Product rotate() throws IOException;
}
