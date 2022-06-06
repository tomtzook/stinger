package com.stinger.framework.db.hibernate.query;

import com.stinger.framework.db.hibernate.JpaHelper;
import com.stinger.framework.db.query.SelectQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JpaSelectQuery<T> implements SelectQuery<T> {

    private final EntityManager mSession;
    private final Class<T> mType;
    private final CriteriaBuilder mBuilder;
    private final Root<T> mTable;
    private final CriteriaQuery<T> mQuery;

    private Expression<Boolean> mWhereClause = null;

    public JpaSelectQuery(EntityManager session, Class<T> type) {
        mSession = session;
        mType = type;
        mBuilder = session.getCriteriaBuilder();
        mQuery = mBuilder.createQuery(type);
        mTable = mQuery.from(type);
        mQuery.select(mTable);
    }

    @Override
    public SelectQuery<T> where(String col, Object value) {
        Path<T> column = getColumn(col);
        updateWhere(mBuilder.equal(column, value));
        return this;
    }

    @Override
    public SelectQuery<T> whereNotNull(String col) {
        Path<T> column = getColumn(col);
        updateWhere(mBuilder.isNotNull(column));
        return this;
    }

    @Override
    public List<T> getAll() throws IOException {
        finalizeQuery();
        return mSession.createQuery(mQuery).getResultList();
    }

    @Override
    public Optional<T> getOne() throws IOException {
        finalizeQuery();
        try {
            return Optional.of(mSession.createQuery(mQuery).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private Path<T> getColumn(String name) {
        name = JpaHelper.convertColumnName(mType, name);
        return mTable.get(name);
    }

    private void updateWhere(Expression<Boolean> expression) {
        if (mWhereClause == null) {
            mWhereClause = expression;
        } else {
            mWhereClause = mBuilder.and(mWhereClause, expression);
        }
    }

    private void finalizeQuery() {
        if (mWhereClause != null) {
            mQuery.where(mWhereClause);
        }
    }
}
