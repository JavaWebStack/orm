package org.javawebstack.orm.test.verification;

import org.javawebstack.orm.test.MySQLConnectionContainer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class Field extends MySQLConnectionContainer {

    ResultSet resultSet;

    public Field(String tableName, String fieldName) throws SQLException {
        String query = String.format("SHOW COLUMNS FROM %s WHERE Field = '%s'", tableName, fieldName);
        resultSet = sql().read(query);
        resultSet.next();
    }

    public void assertPrimaryKey() throws SQLException {
        assert(resultSet.getString("Key").equalsIgnoreCase("PRI"));
    }

    public void assertAutoIncrementing() throws SQLException {
        assert(resultSet.getString("Extra").equalsIgnoreCase("auto_increment"));
    }

    public void assertNullable() throws SQLException {
        assert(resultSet.getString("NULL").equalsIgnoreCase("yes"));
    }

    public void assertNotNullable() throws SQLException {
        assert(resultSet.getString("NULL").equalsIgnoreCase("no"));
    }

    public void assertType(String type) throws SQLException {
        assert(resultSet.getString("Type").equalsIgnoreCase(type));
    }
}
