package stinger.storage;

import com.stinger.framework.storage.Product;
import com.stinger.framework.storage.ProductType;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.StoredProduct;
import stinger.Constants;
import stinger.commands.CommandConfig;

import java.io.IOException;
import java.util.Iterator;

public interface Storage {

    ProductTransaction newTransaction(ProductType type, int priority) throws StorageException;
    String store(ProductType type, int priority, Product product) throws StorageException;
    String store(CommandConfig creatingCommand, ProductType type, int priority, Product product)
            throws StorageException;

    ProductIterator storedProducts() throws StorageException;

    default String store(ProductType type, Product product) throws StorageException {
        return store(type, Constants.PRIORITY_STANDARD, product);
    }

    default String store(CommandConfig creatingCommand, ProductType type, Product product)
            throws StorageException {
        return store(creatingCommand, type, Constants.PRIORITY_STANDARD, product);
    }
}
