package org.javawebstack.orm.wrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQL {

    Connection getConnection();

    ResultSet read(String queryString, Object... parameters) throws SQLException;

    int write(String queryString, Object... parameters) throws SQLException;

    void close(ResultSet resultSet);

}
