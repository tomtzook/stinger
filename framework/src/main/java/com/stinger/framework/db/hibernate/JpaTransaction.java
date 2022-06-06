package com.stinger.framework.db.hibernate;

import com.stinger.framework.db.Transaction;
import com.stinger.framework.db.hibernate.query.JpaSelectQuery;
import com.stinger.framework.db.query.SelectQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JpaTransaction implements Transaction {

    private final EntityManager mSession;
    private final EntityTransaction mTransaction;
    private boolean mAutoCommit;

    public JpaTransaction(EntityManager session) {
        mSession = session;
        mTransaction = session.getTransaction();
        mAutoCommit = false;

        mTransaction.begin();
    }

    @Override
    public <T> List<T> getAll(Class<T> type) throws IOException {
        ensureActive();

        try {
            return select(type).getAll();
        } catch (RuntimeException | Error e) {
            mTransaction.rollback();
            throw e;
        }
    }

    @Override
    public <T, I> Optional<T> getByIdentity(I identity, Class<T> type) throws IOException {
        ensureActive();

        try {
            return Optional.ofNullable(mSession.find(type, identity));
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (RuntimeException | Error e) {
            mTransaction.rollback();
            throw e;
        }
    }

    @Override
    public <T> void add(List<T> list) throws IOException {
        ensureActive();

        try {
            for (T t : list) {
                mSession.persist(t);
            }
        } catch (RuntimeException | Error e) {
            mTransaction.rollback();
            throw e;
        }
    }

    @Override
    public <T> void update(List<T> list) throws IOException {
        ensureActive();

        try {
            for (T t : list) {
                mSession.merge(t);
            }
        } catch (RuntimeException | Error e) {
            mTransaction.rollback();
            throw e;
        }
    }

    @Override
    public <T> void delete(List<T> list) throws IOException {
        ensureActive();

        try {
            for (T t : list) {
                mSession.remove(t);
            }
        } catch (RuntimeException | Error e) {
            mTransaction.rollback();
            throw e;
        }
    }

    @Override
    public <T> SelectQuery<T> select(Class<T> type) throws IOException {
        return new JpaSelectQuery<>(mSession, type);
    }

    @Override
    public void rollback() {
        ensureActive();
        mTransaction.rollback();
    }

    @Override
    public void commit() {
        ensureActive();
        mTransaction.commit();
    }

    @Override
    public void setAutoCommit() {
        ensureActive();
        mAutoCommit = true;
    }

    @Override
    public void close() throws IOException {
        if (mTransaction.isActive()) {
            if (mAutoCommit) {
                commit();
            } else {
                rollback();
            }
        }
    }

    private void ensureActive() {
        if (!mTransaction.isActive()) {
            throw new IllegalStateException("transaction closed");
        }
    }
}
