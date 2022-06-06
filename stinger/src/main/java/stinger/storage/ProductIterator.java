package stinger.storage;

import com.stinger.framework.storage.StoredProduct;

import java.io.Closeable;
import java.io.IOException;

public interface ProductIterator extends Closeable {

    boolean hasNext();
    StoredProduct next() throws IOException;
    void remove() throws IOException;

    @Override
    void close() throws IOException;
}
