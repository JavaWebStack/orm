package eu.bebendorf.ajorm;

import java.sql.Connection;
import java.sql.ResultSet;

public interface SQL {

    Connection getConnection();
    ResultSet read(String queryString,Object... parameters);
    int write(String queryString,Object... parameters);
    void close(ResultSet resultSet);

}
