package org.javawebstack.orm.connection;

import org.javawebstack.orm.renderer.QueryStringRenderer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQL {

    Connection getConnection();

    ResultSet read(String queryString, Object... parameters) throws SQLException;

    int write(String queryString, Object... parameters) throws SQLException;

    void close(ResultSet resultSet);
    void close();

    QueryStringRenderer builder();

    void addQueryLogger(QueryLogger logger);

    void removeQueryLogger(QueryLogger logger);

    @Deprecated
    SQL fork();

}
