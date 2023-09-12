package org.javawebstack.orm.connection.pool;

import org.javawebstack.orm.connection.QueryLogger;
import org.javawebstack.orm.connection.SQL;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

public class SQLPool {

    private final PoolScaling scaling;
    private final Supplier<SQL> supplier;
    private final List<SQL> connections = new ArrayList<>();
    private final Queue<SQL> connectionQueue = new LinkedBlockingQueue<>();
    private final Set<QueryLogger> loggers = new HashSet<>();
    private boolean closing;
    private PoolQueryLogger queryLogger = new PoolQueryLogger();

    public SQLPool(PoolScaling scaling, Supplier<SQL> supplier) {
        this.scaling = scaling;
        this.supplier = supplier;
        this.scale();
    }

    public SQLPool(int min, int max, Supplier<SQL> supplier) {
        this(new MinMaxScaler(min, max), supplier);
    }

    public PooledSQL get() {
        if(closing)
            throw new IllegalStateException("Pool has already been closed");
        scale();
        SQL sql = connectionQueue.poll();
        scale();
        return new PooledSQL(this, sql);
    }

    public void release(SQL sql) {
        if(!closing) {
            if(sql instanceof PooledSQL) {
                sql.close();
                return;
            }
            if(!connections.contains(sql))
                throw new IllegalArgumentException("Not a connection of this pool");
            connectionQueue.add(sql);
            scale();
        }
    }

    public void addQueryLogger(QueryLogger logger) {
        loggers.add(logger);
    }

    public void removeQueryLogger(QueryLogger logger) {
        loggers.remove(logger);
    }

    public void scale() {
        if(closing)
            throw new IllegalStateException("Pool has already been closed");
        int newScale = scaling.scale(connections.size(), connections.size() - connectionQueue.size());
        if(newScale == connections.size())
            return;
        while (newScale > connections.size()) {
            SQL sql = supplier.get();
            sql.addQueryLogger(queryLogger);
            connections.add(sql);
            connectionQueue.add(sql);
        }
        while (newScale < connections.size() && connectionQueue.size() > 0) {
            SQL sql = connectionQueue.poll();
            sql.close();
            connections.remove(sql);
        }
    }

    public void close() {
        closing = true;
        for(SQL connection : connections) {
            connection.close();
        }
    }

    private class PoolQueryLogger implements QueryLogger {

        public void log(String query, Object[] parameters) {
            for(QueryLogger logger : loggers)
                logger.log(query, parameters);
        }

    }

}
