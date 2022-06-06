package stinger.storage;

import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.WritableProductMetadata;

import java.io.Closeable;
import java.nio.channels.WritableByteChannel;

public interface ProductTransaction extends WritableByteChannel, Closeable {

    void putMetadataProperty(String name, Object value);

    void commit() throws StorageException;
}
