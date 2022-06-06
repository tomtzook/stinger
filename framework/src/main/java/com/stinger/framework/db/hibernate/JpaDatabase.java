package com.stinger.framework.db.hibernate;

import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Database;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;

public class JpaDatabase implements Database {

    private final EntityManagerFactory mFactory;

    public JpaDatabase(String unitName) {
        mFactory = Persistence.createEntityManagerFactory(unitName);
    }

    @Override
    public Connection open() throws IOException {
        return new JpaConnection(mFactory.createEntityManager());
    }

    @Override
    public void close() throws IOException {
        mFactory.close();
    }
}
