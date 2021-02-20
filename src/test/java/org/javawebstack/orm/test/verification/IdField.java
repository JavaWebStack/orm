package org.javawebstack.orm.test.verification;

import org.javawebstack.orm.test.ORMTestCase;

import java.sql.SQLException;

// Extending the ORMTestCase Class to obtain the database connection
public class IdField extends ORMTestCase {

    public static void assertCorrectDatabaseFormat(String table_name) throws SQLException {
        Field field = new Field(table_name, "id");

        field.assertAutoIncrementing();
        field.assertNotNullable();
        field.assertPrimaryKey();
        field.assertType("int(11)");

    }

}
