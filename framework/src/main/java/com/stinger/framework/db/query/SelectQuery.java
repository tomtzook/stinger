package com.stinger.framework.db.query;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SelectQuery<T> {

    SelectQuery<T> where(String col, Object value);
    SelectQuery<T> whereNotNull(String col);

    SelectQuery<T> orderBy(String col, boolean descending);

    List<T> getAll() throws IOException;
    Optional<T> getOne() throws IOException;
}
