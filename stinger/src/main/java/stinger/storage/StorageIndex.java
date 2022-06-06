package stinger.storage;

import com.castle.util.closeables.Closer;
import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Database;
import com.stinger.framework.db.Transaction;
import com.stinger.framework.db.hibernate.JpaDatabase;
import com.stinger.framework.logging.Logger;
import com.stinger.framework.storage.InFileStoredProduct;
import com.stinger.framework.storage.ProductMetadata;
import com.stinger.framework.storage.ProductSerializer;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.StoredProduct;
import stinger.storage.model.StoredProductModel;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

public class StorageIndex {

    private final Database mDatabase;
    private final Logger mLogger;
    private final ProductSerializer mProductSerializer;

    public StorageIndex(Database database, Logger logger) throws StorageException {
        mDatabase = database;
        mLogger = logger;
        mProductSerializer = new ProductSerializer(StandardProductType::fromInt);
    }

    public static StorageIndex fromConfig(String configName, Logger logger) throws StorageException {
        try {
            Database database = new JpaDatabase(configName);
            return new StorageIndex(database, logger);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public ProductIndexTransaction addProduct(ProductMetadata metadata, Path dataPath) throws StorageException {
        try {
            Connection connection = mDatabase.open();
            try {
                return new ProductIndexTransaction(
                        connection,
                        metadata,
                        dataPath,
                        mProductSerializer);
            } catch (IOException e) {
                connection.close();
                throw e;
            }
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public ProductIterator storedProductsSnapshot() throws StorageException {
        try {
            Connection connection = mDatabase.open();
            try {
                return new ProductsIteratorImpl(connection, mProductSerializer);
            } catch (IOException e) {
                try {
                    connection.close();
                } catch (IOException e1) {
                    e.addSuppressed(e1);
                }

                throw e;
            }
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    private static class ProductsIteratorImpl implements ProductIterator {

        private final Connection mConnection;
        private final ProductSerializer mProductSerializer;
        private final Transaction mTransaction;

        private final List<StoredProductModel> mModels;
        private int mCurrentIndex = -1;

        private ProductsIteratorImpl(Connection connection,
                                     ProductSerializer productSerializer)
                throws IOException {
            mConnection = connection;
            mProductSerializer = productSerializer;
            mTransaction = connection.openTransaction();

            mModels = mTransaction.select(StoredProductModel.class).getAll();
        }

        @Override
        public boolean hasNext() {
            return mCurrentIndex < mModels.size() - 1;
        }

        @Override
        public StoredProduct next() throws IOException {
            mCurrentIndex++;
            if (mCurrentIndex < 0 || mCurrentIndex >= mModels.size()) {
                throw new NoSuchElementException();
            }

            StoredProductModel model = mModels.get(mCurrentIndex);

            ProductMetadata metadata = mProductSerializer.deserializeMetadata(model.getMetadata());
            return new InFileStoredProduct(
                    metadata,
                    Paths.get(model.getPath()));
        }

        @Override
        public void remove() throws IOException {
            if (mCurrentIndex < 0 || mCurrentIndex >= mModels.size()) {
                throw new NoSuchElementException();
            }

            StoredProductModel model = mModels.get(mCurrentIndex);
            mTransaction.delete(model);
        }

        @Override
        public void close() throws IOException {
            mTransaction.commit();

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
}
