package stinger.logging;

import com.stinger.framework.storage.Product;

import java.io.IOException;

public interface LoggerControl {

    long getRecordCount();
    Product rotate() throws IOException;
}
