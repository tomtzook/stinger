package stinger.storage;

import stinger.commands.CommandConfig;
import stingerlib.storage.Product;
import stingerlib.storage.StorageException;
import stingerlib.storage.StoredProduct;

import java.util.Iterator;

public interface Storage {

    String store(Product product) throws StorageException;
    String store(CommandConfig creator, Product product) throws StorageException;
    Iterator<StoredProduct> storedProducts() throws StorageException;
}
