package stinger.storage;

import com.castle.util.closeables.Closer;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.WritableProductMetadata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PersistentProductTransaction implements ProductTransaction {

    private final ProductIndexTransaction mTransaction;
    private final WritableProductMetadata mMetadata;
    private final SeekableByteChannel mChannel;

    public PersistentProductTransaction(ProductIndexTransaction transaction,
                                        WritableProductMetadata metadata,
                                        Path path) throws IOException {
        mTransaction = transaction;
        mMetadata = metadata;
        mChannel = Files.newByteChannel(path,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE);
    }

    @Override
    public void putMetadataProperty(String name, Object value) {
        mMetadata.putProperty(name, value);
    }

    @Override
    public void commit() throws StorageException {
        try {
            mMetadata.setContentSize(mChannel.size());
            mChannel.close();
        } catch (IOException e) {
            throw new StorageException(e);
        }

        mTransaction.commit();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return mChannel.write(src);
    }

    @Override
    public boolean isOpen() {
        return mChannel.isOpen();
    }

    @Override
    public void close() throws IOException {
        Closer closer = Closer.empty();
        if (mChannel.isOpen()) {
            closer.add(mChannel);
            mTransaction.rollback();
        }

        closer.add(mTransaction);

        try {
            closer.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
