package org.javawebstack.orm.test.shared.verification;

import org.javawebstack.orm.test.shared.settings.MySQLConnectionContainer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Field extends MySQLConnectionContainer {

    String tableName;
    String fieldName;
    ResultSet resultSet;

    public Field(String tableName, String fieldName) throws SQLException {
        this.tableName = tableName;
        this.fieldName = fieldName;

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

    public void assertType(String expectedType) throws SQLException {
        String actualType = resultSet.getString("Type");
        assertTrue(
                actualType.equalsIgnoreCase(expectedType),
                String.format("The type of %s.%s is %s, but type %s was expected.", tableName, fieldName, actualType, expectedType)
        );
    }
}
