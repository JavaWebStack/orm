package org.javawebstack.orm.connection;

public interface QueryLogger {

    void log(String query, Object[] parameters);

}
