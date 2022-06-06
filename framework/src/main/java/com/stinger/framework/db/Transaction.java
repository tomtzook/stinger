package com.stinger.framework.db;

import com.stinger.framework.db.query.SelectQuery;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface Transaction extends Closeable {

    <T> SelectQuery<T> select(Class<T> type) throws IOException;

    <T> List<T> getAll(Class<T> type) throws IOException;
    <T> Optional<T> getFirst(Class<T> type) throws IOException;
    <T, I> Optional<T> getByIdentity(I identity, Class<T> type) throws IOException;
    <T> void add(List<T> list) throws IOException;
    <T> void update(List<T> list) throws IOException;
    <T> void delete(List<T> list) throws IOException;

    void rollback();
    void commit();

    void setAutoCommit();

    @SuppressWarnings("unchecked")
    default <T> void add(T... ts) throws IOException {
        add(Arrays.asList(ts));
    }

    @SuppressWarnings("unchecked")
    default <T> void update(T... ts) throws IOException {
        update(Arrays.asList(ts));
    }

    @SuppressWarnings("unchecked")
    default <T> void delete(T... ts) throws IOException {
        delete(Arrays.asList(ts));
    }
}
