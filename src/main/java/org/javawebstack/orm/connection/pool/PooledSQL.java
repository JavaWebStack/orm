package org.javawebstack.orm.connection.pool;

import org.javawebstack.orm.connection.QueryLogger;
import org.javawebstack.orm.connection.SQL;
import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.renderer.QueryStringRenderer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PooledSQL implements SQL, AutoCloseable {

    private final SQLPool pool;
    private final SQL connection;
    private boolean closed;

    public PooledSQL(SQLPool pool, SQL connection) {
        this.pool = pool;
        this.connection = connection;
    }

    private void assureOpen() {
        if(closed)
            throw new ORMQueryException("Pooled connection has already been returned");
    }

    public Connection getConnection() {
        assureOpen();
        return null;
    }

    public ResultSet read(String queryString, Object... parameters) throws SQLException {
        assureOpen();
        return connection.read(queryString, parameters);
    }

    public int write(String queryString, Object... parameters) throws SQLException {
        assureOpen();
        return connection.write(queryString, parameters);
    }

    public void close(ResultSet resultSet) {
        assureOpen();
        connection.close(resultSet);
    }

    public void close() {
        pool.release(connection);
        closed = true;
    }

    public QueryStringRenderer builder() {
        assureOpen();
        return connection.builder();
    }

    public void addQueryLogger(QueryLogger logger) {
        throw new RuntimeException("addQueryLogger can not be executed on a pooled connection, call it on the pool instead");
    }

    public void removeQueryLogger(QueryLogger logger) {
        throw new RuntimeException("removeQueryLogger can not be executed on a pooled connection, call it on the pool instead");
    }

    public SQL fork() {
        throw new ORMQueryException("Forking is not supported by pooled connections");
    }

}
