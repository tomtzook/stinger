package stinger.storage;

import com.stinger.framework.storage.Product;
import com.stinger.framework.storage.ProductType;
import com.stinger.framework.storage.StorageException;
import stinger.Constants;
import stinger.commands.CommandConfig;

public interface Storage {

    ProductTransaction newTransaction(ProductType type, int priority) throws StorageException;
    void store(ProductType type, int priority, Product product) throws StorageException;
    void store(CommandConfig creatingCommand, ProductType type, int priority, Product product)
            throws StorageException;

    ProductIterator storedProducts() throws StorageException;

    default void store(ProductType type, Product product) throws StorageException {
        store(type, Constants.PRIORITY_STANDARD, product);
    }

    default void store(CommandConfig creatingCommand, ProductType type, Product product)
            throws StorageException {
        store(creatingCommand, type, Constants.PRIORITY_STANDARD, product);
    }
}
