package stinger.meta;

import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Database;
import com.stinger.framework.db.Transaction;
import com.stinger.framework.db.hibernate.JpaDatabase;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class ToolMetaStore {

    private final Database mDatabase;

    public ToolMetaStore(Database database) {
        mDatabase = database;
    }

    public static ToolMetaStore fromConfig(String configName) {
        Database database = new JpaDatabase(configName);
        return new ToolMetaStore(database);
    }

    public ToolMeta modifyMeta(Function<Optional<ToolMeta>, ToolMeta> function) throws IOException {
        try (Connection connection = mDatabase.open();
             Transaction transaction = connection.openTransaction()) {
            Optional<ToolMeta> optional = getStoredMeta(transaction);
            ToolMeta meta = function.apply(optional);

            if (optional.isPresent()) {
                //noinspection OptionalGetWithoutIsPresent
                MetaModel model = transaction.getFirst(MetaModel.class).get();
                model.setVersion(meta.getVersion());

                transaction.update(model);
            } else {
                MetaModel model = new MetaModel();
                model.setId(meta.getId());
                model.setVersion(meta.getVersion());

                transaction.add(model);
            }

            transaction.commit();

            return meta;
        }
    }

    public Optional<ToolMeta> getMeta() throws IOException {
        try (Connection connection = mDatabase.open();
             Transaction transaction = connection.openTransaction()) {
            return getStoredMeta(transaction);
        }
    }

    private Optional<ToolMeta> getStoredMeta(Transaction transaction) throws IOException {
        Optional<MetaModel> optional = transaction.getFirst(MetaModel.class);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        MetaModel model = optional.get();
        ToolMeta meta = new ToolMeta(
                model.getId(),
                model.getVersion()
        );

        return Optional.of(meta);
    }
}
