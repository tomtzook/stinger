package com.stinger.framework.db;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface Database extends Closeable {

    List<Map<String, Object>> query(String query, Object... args) throws DatabaseException;
    void update(String query, Object... args) throws DatabaseException;
}
