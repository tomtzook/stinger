package stinger.storage;

import stinger.commands.CommandConfig;
import stingerlib.storage.InFileStoredProduct;
import stingerlib.storage.Product;
import stingerlib.storage.StorageException;
import stingerlib.storage.StoredProduct;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
    public String store(Product product) throws StorageException {
        return store(null, product);
    }

    @Override
    public String store(CommandConfig creator, Product product) throws StorageException {
        try {
            InFileStoredProduct storedProduct = storeNew(product);
            mStorageIndex.addProduct(
                    storedProduct,
                    creator != null ? creator.getId() : null);
            return storedProduct.getId();
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public Iterator<StoredProduct> storedProducts() throws StorageException {
        return mStorageIndex.storedProductsSnapshot();
    }

    private InFileStoredProduct storeNew(Product product) throws IOException {
        String id = generateId();
        Path path = saveProductData(product, id);

        return new InFileStoredProduct(id, product.getType(), path);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private Path saveProductData(Product product, String id) throws IOException {
        Path file = mRoot.resolve(id);
        try (InputStream inputStream = product.open()) {
            Files.copy(inputStream, file);
        }

        return file;
    }
}
