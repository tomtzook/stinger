package stinger.apps.control.store;

import com.castle.util.closeables.Closer;
import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Transaction;

import java.io.Closeable;
import java.io.IOException;

public abstract class AppTransaction implements Closeable {

    private final Connection mConnection;
    private final Transaction mTransaction;

    protected AppTransaction(Connection connection, Transaction transaction) {
        mConnection = connection;
        mTransaction = transaction;
    }

    public void commit() {
        mTransaction.commit();
    }

    @Override
    public void close() throws IOException {
        Closer closer = Closer.empty();

        closer.add(mTransaction);
        closer.add(mConnection);

        try {
            closer.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
