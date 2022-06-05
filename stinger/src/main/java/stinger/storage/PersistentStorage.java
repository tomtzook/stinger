package stinger.storage;

import com.stinger.framework.storage.GenericWritableProductMetadata;
import com.stinger.framework.storage.InFileStoredProduct;
import com.stinger.framework.storage.Product;
import com.stinger.framework.storage.ProductMetadata;
import com.stinger.framework.storage.ProductType;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.StoredProduct;
import com.stinger.framework.storage.WritableProductMetadata;
import stinger.commands.CommandConfig;
import stinger.util.IoStreams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.UUID;

public class PersistentStorage implements Storage {

    private final Path mRoot;
    private final StorageIndex mStorageIndex;

    public PersistentStorage(Path root, StorageIndex storageIndex) {
        mRoot = root;
        mStorageIndex = storageIndex;
    }

    @Override
    public ProductTransaction newTransaction(ProductType type) throws StorageException {
        String id = generateId();
        WritableProductMetadata metadata =
                new GenericWritableProductMetadata(id, type);
        Path file = mRoot.resolve(id);

        mStorageIndex.addProduct(file, metadata);
        try {
            return new PersistentProductTransaction(
                    this,
                    metadata,
                    file);
        } catch (IOException e) {
            mStorageIndex.rollbackProduct(file, metadata);
            throw new StorageException(e);
        }
    }

    @Override
    public String store(ProductType type, Product product) throws StorageException {
        try (ProductTransaction transaction = newTransaction(type);
             InputStream productStream = product.open();
             ReadableByteChannel inChannel = Channels.newChannel(productStream)) {
            IoStreams.copy(inChannel, transaction);
            transaction.commit();

            return transaction.getMetadata().getId();
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public Iterator<StoredProduct> storedProducts() throws StorageException {
        return mStorageIndex.storedProductsSnapshot();
    }

    public void commit(PersistentProductTransaction transaction) throws StorageException {
        mStorageIndex.commitProduct(transaction.getMetadata());
    }

    public void rollback(PersistentProductTransaction transaction) throws StorageException {
        mStorageIndex.rollbackProduct(transaction.getPath(), transaction.getMetadata());
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
