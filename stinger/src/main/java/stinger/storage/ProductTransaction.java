package stinger.storage;

import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.WritableProductMetadata;

import java.io.Closeable;
import java.nio.channels.WritableByteChannel;

public interface ProductTransaction extends WritableByteChannel, Closeable {

    WritableProductMetadata getMetadata();

    void commit() throws StorageException;
}
