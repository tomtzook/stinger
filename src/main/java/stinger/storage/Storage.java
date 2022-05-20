package stinger.storage;

import stingerlib.storage.Product;
import stingerlib.storage.StorageException;
import stingerlib.storage.StoredProduct;

import java.util.Iterator;

public interface Storage {

    String store(Product product) throws StorageException;
    Iterator<StoredProduct> storedProducts() throws StorageException;
}
