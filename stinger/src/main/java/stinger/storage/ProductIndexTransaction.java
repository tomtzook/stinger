package stinger.storage;

import com.castle.util.closeables.Closer;
import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Transaction;
import com.stinger.framework.storage.ProductMetadata;
import com.stinger.framework.storage.ProductSerializer;
import com.stinger.framework.storage.StorageException;
import stinger.storage.model.StoredProductModel;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProductIndexTransaction implements Closeable {

    private final Connection mConnection;
    private final ProductMetadata mMetadata;
    private final Path mDataPath;
    private final ProductSerializer mProductSerializer;
    private final Transaction mTransaction;
    private final StoredProductModel mModel;

    public ProductIndexTransaction(Connection connection,
                                   ProductMetadata metadata,
                                   Path dataPath,
                                   ProductSerializer productSerializer) throws IOException {
        mConnection = connection;
        mMetadata = metadata;
        mDataPath = dataPath;
        mProductSerializer = productSerializer;

        mTransaction = connection.openTransaction();
        try {
            mModel = new StoredProductModel();
            mModel.setProductId(metadata.getId());
            mModel.setType((StandardProductType) metadata.getType());
            mModel.setPath(dataPath.toAbsolutePath().toString());
            mModel.setMetadata(null);
            mModel.setCommited(false);

            mTransaction.add(mModel);
        } catch (IOException e) {
            mTransaction.close();
            throw e;
        }
    }

    public void commit() throws StorageException {
        try {
            byte[] metadata = mProductSerializer.serialize(mMetadata);
            mModel.setMetadata(metadata);
            mModel.setCommited(true);

            mTransaction.commit();
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public void rollback() throws IOException {
        mTransaction.rollback();
        Files.deleteIfExists(mDataPath);
    }

    @Override
    public void close() throws IOException {
        Closer closer = Closer.empty();

        closer.add(mTransaction);
        closer.add(mConnection);

        try {
            closer.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
