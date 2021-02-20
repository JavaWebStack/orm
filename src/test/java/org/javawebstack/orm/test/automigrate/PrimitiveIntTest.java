package org.javawebstack.orm.test.automigrate;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.verification.Field;
import org.javawebstack.orm.test.verification.IdField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class PrimitiveIntTest extends ORMTestCase {

    @BeforeEach
    public void setUp() throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(PrimitiveIntContainer.class, sql(), config);
        ORM.autoMigrate(true);
    }

    @Test
    public void testPrimitiveIntegerId() throws ORMConfigurationException, SQLException {
        IdField.assertCorrectDatabaseFormat("primitive_int_containers");
    }

    @Test
    public void testPrimitiveIntegerDatatype() throws ORMConfigurationException, SQLException {
        Field field = new Field("primitive_int_containers", "field");
        field.assertType("int(11)");
    }

    public static class PrimitiveIntContainer extends Model {
        @Column
        int id;

        @Column
        int field;
    }
}
