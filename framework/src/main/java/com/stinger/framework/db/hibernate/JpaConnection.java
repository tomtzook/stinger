package com.stinger.framework.db.hibernate;

import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Transaction;
import jakarta.persistence.EntityManager;

import java.io.IOException;

public class JpaConnection implements Connection {

    private final EntityManager mSession;

    public JpaConnection(EntityManager session) {
        mSession = session;
    }

    @Override
    public Transaction openTransaction() {
        if (mSession.getTransaction().isActive()) {
            throw new IllegalStateException("nested transactions not supported");
        }

        return new JpaTransaction(mSession);
    }

    @Override
    public void close() throws IOException {
        mSession.close();
    }
}
