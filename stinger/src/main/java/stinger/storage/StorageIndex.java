package stinger.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.stinger.framework.db.Database;
import com.stinger.framework.db.DatabaseException;
import com.stinger.framework.db.JdbcDatabase;
import com.stinger.framework.logging.Logger;
import com.stinger.framework.storage.GenericProductMetadata;
import com.stinger.framework.storage.InFileStoredProduct;
import com.stinger.framework.storage.ProductJsonSerializer;
import com.stinger.framework.storage.ProductMetadata;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.StoredProduct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class StorageIndex {

    private final Database mDatabase;
    private final Logger mLogger;
    private final ProductJsonSerializer mProductJsonSerializer;

    public StorageIndex(Database database, Logger logger) throws StorageException {
        mDatabase = database;
        mLogger = logger;
        mProductJsonSerializer = new ProductJsonSerializer(StandardProductType::fromInt);

        try {
            createTable();
        } catch (DatabaseException e) {
            throw new StorageException(e);
        }
    }

    public static StorageIndex inFile(Path dbFile, Logger logger) throws StorageException {
        try {
            if (!Files.exists(dbFile)) {
                Files.createFile(dbFile);
            }

            Database database = JdbcDatabase.open(dbFile);
            return new StorageIndex(database, logger);
        } catch (ClassNotFoundException | SQLException | IOException e) {
            throw new StorageException(e);
        }
    }

    public void addProduct(Path dataPath, ProductMetadata metadata) throws StorageException {
        try {
            mDatabase.update("INSERT INTO pros (prid, type, path, is_commited) VALUES (?,?,?,?)",
                    metadata.getId(),
                    metadata.getType().intValue(),
                    dataPath.toAbsolutePath().toString(),
                    false);
        } catch (DatabaseException e) {
            throw new StorageException(e);
        }
    }

    public void commitProduct(ProductMetadata metadata) throws StorageException {
        try {
            String metadataJson = metadataToJson(metadata);
            mDatabase.update("UPDATE pros SET metadata=%s, is_commited=%s WHERE prid=%s",
                    metadataJson,
                    true,
                    metadata.getId());
        } catch (DatabaseException e) {
            throw new StorageException(e);
        }
    }

    public void rollbackProduct(Path dataPath, ProductMetadata metadata) {
        try {
            mDatabase.update("DELETE FROM pros WHERE prid=?", metadata.getId());
        } catch (DatabaseException e) {
            mLogger.error("Storage remove index error", e);
        }

        try {
            Files.deleteIfExists(dataPath);
        } catch (IOException e) {
            mLogger.error("Storage remove path error", e);
        }
    }

    public Iterator<StoredProduct> storedProductsSnapshot() throws StorageException {
        try {
            List<Map<String, Object>> data = mDatabase.query("SELECT * FROM pros");
            List<InFileStoredProduct> storedProducts = parseProducts(data);

            return new Iterator<>() {
                int currentIndex = -1;

                @Override
                public boolean hasNext() {
                    return currentIndex < storedProducts.size() - 1;
                }

                @Override
                public StoredProduct next() {
                    currentIndex++;
                    if (currentIndex < 0 || currentIndex >= storedProducts.size()) {
                        throw new NoSuchElementException();
                    }

                    return storedProducts.get(currentIndex);
                }

                @Override
                public void remove() {
                    if (currentIndex < 0 || currentIndex >= storedProducts.size()) {
                        throw new NoSuchElementException();
                    }

                    StorageIndex.this.remove(storedProducts.get(currentIndex));
                }
            };
        } catch (DatabaseException e) {
            throw new StorageException(e);
        }
    }

    private void createTable() throws DatabaseException {
        mDatabase.update(String.format("CREATE TABLE IF NOT EXISTS pros (%s,%s,%s,%s,%s)",
                "prid NVARCHAR UNIQUE NOT NULL",
                "type INT NOT NULL",
                "path NVARCHAR NOT NULL",
                "metadata NVARCHAR NULL",
                "is_commited BOOL NOT NULL"));
    }

    private void remove(InFileStoredProduct product) {
        try {
            mDatabase.update("DELETE FROM pros WHERE prid=?", product.getMetadata().getId());
        } catch (DatabaseException e) {
            mLogger.error("Storage remove index error", e);
        }

        try {
            Files.deleteIfExists(product.getDataPath());
        } catch (IOException e) {
            mLogger.error("Storage remove path error", e);
        }
    }

    private List<InFileStoredProduct> parseProducts(List<Map<String, Object>> data) throws StorageException {
        List<InFileStoredProduct> products = new ArrayList<>();

        for (Map<String, Object> map : data) {
            //String id = (String) map.get("prid");
            //int typeInt = (int) map.get("type");
            String pathStr = (String) map.get("path");
            String metadataJson = (String) map.get("metadata");
            ProductMetadata metadata = jsonToMetadata(metadataJson);

            InFileStoredProduct storedProduct = new InFileStoredProduct(metadata, Paths.get(pathStr));
            products.add(storedProduct);
        }

        return products;
    }

    private String metadataToJson(ProductMetadata metadata) throws StorageException {
        try {
            JsonElement element = mProductJsonSerializer.serialize(metadata);
            return element.toString();
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    private ProductMetadata jsonToMetadata(String json) throws StorageException {
        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);

            return mProductJsonSerializer.deserialize(element);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }
}
