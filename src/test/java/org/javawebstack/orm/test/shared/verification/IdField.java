package org.javawebstack.orm.test.shared.verification;

import org.javawebstack.orm.test.ORMTestCase;

import java.sql.SQLException;

// Extending the ORMTestCase Class to obtain the database connection
public class IdField extends ORMTestCase {

    public static void assertCorrectDatabaseFormat(String tableName) throws SQLException {
        Field field = new Field(tableName, "id");

        field.assertAutoIncrementing();
        field.assertNotNullable();
        field.assertPrimaryKey();
        field.assertType("int(11)");

    }

}
