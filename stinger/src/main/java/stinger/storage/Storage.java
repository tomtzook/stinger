package stinger.storage;

import com.stinger.framework.storage.Product;
import com.stinger.framework.storage.ProductType;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.StoredProduct;
import stinger.commands.CommandConfig;

import java.io.IOException;
import java.util.Iterator;

public interface Storage {

    ProductTransaction newTransaction(ProductType type) throws StorageException;
    String store(ProductType type, Product product) throws StorageException;
    String store(CommandConfig creatingCommand, ProductType type, Product product) throws StorageException;

    Iterator<StoredProduct> storedProducts() throws StorageException;
}
