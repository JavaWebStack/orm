package org.javawebstack.orm.connection.pool;

public interface PoolScaling {

    int scale(int total, int used);

}
