package stinger.storage;

import stinger.commands.CommandConfig;
import com.stinger.framework.storage.Product;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.StoredProduct;

import java.util.Iterator;

public interface Storage {

    String store(Product product) throws StorageException;
    String store(CommandConfig creator, Product product) throws StorageException;
    Iterator<StoredProduct> storedProducts() throws StorageException;
}
