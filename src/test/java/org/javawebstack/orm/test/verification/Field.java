package org.javawebstack.orm.test.verification;

import org.javawebstack.orm.test.DatabaseConnection;
import org.javawebstack.orm.test.ORMTestCase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Field extends DatabaseConnection {

    ResultSet resultSet;

    public Field(String tableName, String fieldName) throws SQLException {
        String query = String.format("SHOW COLUMNS FROM %s WHERE Field = '%s'", tableName, fieldName);
        resultSet = sql().read(query);
        resultSet.next();
    }

    public void assertPrimaryKey() throws SQLException {
        assert(resultSet.getString("Key").equals("PRI"));
    }

    public void assertAutoIncrementing() throws SQLException {
        assert(resultSet.getString("Extra").equals("auto_increment"));
    }

    public void assertNullable() throws SQLException {
        assert(resultSet.getString("NULL").equals("YES"));
    }

    public void assertNotNullable() throws SQLException {
        assert(resultSet.getString("NULL").equals("NO"));
    }

    public void assertType(String type) throws SQLException {
        assert(resultSet.getString("Type").equals(type));
    }
}
