package stinger.storage;

import com.stinger.framework.storage.GenericWritableProductMetadata;
import com.stinger.framework.storage.Product;
import com.stinger.framework.storage.ProductType;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.WritableProductMetadata;
import stinger.commands.CommandConfig;
import stinger.util.IoStreams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
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
        Path dataPath = mRoot.resolve(id);

        try {
            return new PersistentProductTransaction(
                    mStorageIndex.addProduct(metadata, dataPath),
                    metadata,
                    dataPath);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public String store(ProductType type, Product product) throws StorageException {
        return store(null, type, product);
    }

    @Override
    public String store(CommandConfig creatingCommand, ProductType type, Product product) throws StorageException {
        try (ProductTransaction transaction = newTransaction(type);
             InputStream productStream = product.open();
             ReadableByteChannel inChannel = Channels.newChannel(productStream)) {
            if (creatingCommand != null) {
                transaction.getMetadata().putProperty("commandId", creatingCommand.getId());
            }

            IoStreams.copy(inChannel, transaction);
            transaction.commit();

            return transaction.getMetadata().getId();
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public ProductIterator storedProducts() throws StorageException {
        return mStorageIndex.storedProductsSnapshot();
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
