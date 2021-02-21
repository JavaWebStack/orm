package org.javawebstack.orm.test.shared.verification;

import org.javawebstack.orm.test.shared.settings.MySQLConnectionContainer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertTrue(
                resultSet.getString("Key").equalsIgnoreCase("PRI"),
                String.format("%s.%s should be a Primary Key but was not.", tableName, fieldName)
        );
    }

    public void assertNotPrimaryKey() throws SQLException {
        assertFalse(
                resultSet.getString("Key").equalsIgnoreCase("PRI"),
                String.format("%s.%s should not be a Primary Key but was.", tableName, fieldName)
        );
    }

    public void assertAutoIncrementing() throws SQLException {
        assertTrue(
                resultSet.getString("Extra").equalsIgnoreCase("auto_increment"),
                String.format("%s.%s should be auto incrementing but was not.", tableName, fieldName)
        );
    }

    public void assertNotAutoIncrementing() throws SQLException {
        assertFalse(
                resultSet.getString("Extra").equalsIgnoreCase("auto_increment"),
                String.format("%s.%s should not be auto incrementing but was.", tableName, fieldName)
        );
    }

    public void assertNullable() throws SQLException {
        assertTrue(
                resultSet.getString("NULL").equalsIgnoreCase("yes"),
                String.format("%s.%s is not nullable but nullability was expected.", tableName, fieldName)
        );
    }

    public void assertNotNullable() throws SQLException {
        assertTrue(
                resultSet.getString("NULL").equalsIgnoreCase("no"),
                String.format("%s.%s is nullable but no nullability was expected.", tableName, fieldName)
        );
    }

    public void assertType(String expectedType) throws SQLException {
        String actualType = resultSet.getString("Type");
        assertTrue(
                actualType.equalsIgnoreCase(expectedType),
                String.format("The type of %s.%s is %s, but type %s was expected.", tableName, fieldName, actualType, expectedType)
        );
    }
}
