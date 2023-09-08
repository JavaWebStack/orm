package org.javawebstack.orm.connection.pool;

import org.javawebstack.orm.connection.SQL;

public class SingletonPool extends SQLPool {

    public SingletonPool(SQL sql) {
        super(new MinMaxScaler(1,1), () -> sql);
    }

}
