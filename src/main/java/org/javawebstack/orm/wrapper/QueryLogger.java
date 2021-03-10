package org.javawebstack.orm.wrapper;

public interface QueryLogger {

    void log(String query, Object[] parameters);

}
