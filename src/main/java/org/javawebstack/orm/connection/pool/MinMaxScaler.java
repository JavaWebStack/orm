package org.javawebstack.orm.connection.pool;

public class MinMaxScaler implements PoolScaling {

    private final int min;
    private final int max;

    public MinMaxScaler(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int scale(int total, int used) {
        int needed = used + 1;
        return Math.max(min, Math.min(max, needed));
    }

}
