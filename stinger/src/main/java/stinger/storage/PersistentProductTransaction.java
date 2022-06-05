package stinger.storage;

import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.WritableProductMetadata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PersistentProductTransaction implements ProductTransaction {

    private final PersistentStorage mStorage;
    private final WritableProductMetadata mMetadata;
    private final Path mPath;
    private final SeekableByteChannel mChannel;

    public PersistentProductTransaction(PersistentStorage storage,
                                        WritableProductMetadata metadata,
                                        Path path) throws IOException {
        mStorage = storage;
        mMetadata = metadata;
        mPath = path;
        mChannel = Files.newByteChannel(path,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE);
    }

    @Override
    public WritableProductMetadata getMetadata() {
        return mMetadata;
    }

    @Override
    public void commit() throws StorageException {
        try {
            mChannel.close();
        } catch (IOException e) {
            throw new StorageException(e);
        }

        mStorage.commit(this);
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
        if (mChannel.isOpen()) {
            mChannel.close();

            try {
                mStorage.rollback(this);
            } catch (StorageException e) {
                throw new IOException(e);
            }
        }
    }

    public Path getPath() {
        return mPath;
    }
}
