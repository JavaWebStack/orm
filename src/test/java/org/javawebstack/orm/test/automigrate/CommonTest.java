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
import java.util.HashMap;
import java.util.Map;

public class CommonTest extends ORMTestCase {

    private static final String tableName = "datatypes";

    @BeforeEach
    public void setUp() throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(Datatype.class, sql(), config);
        ORM.autoMigrate(true);
    }



    @Test
    public void testId() throws ORMConfigurationException, SQLException {
        IdField.assertCorrectDatabaseFormat(tableName);
    }

    @Test
    public void testPrimitiveIntegerDatatype() throws ORMConfigurationException, SQLException {
        Field checkedField;

        Map<String, String> columnDataTypeMap = new HashMap<>();

        columnDataTypeMap.put("primitive_boolean", "tinyint(1)");
        columnDataTypeMap.put("wrapper_boolean", "tinyint(1)");

        columnDataTypeMap.put("primitive_byte", "tinyint(3)");
        columnDataTypeMap.put("primitive_wrapper", "tinyint(3)");

        for(Map.Entry<String, String> entry : columnDataTypeMap.entrySet()) {
            checkedField = new Field(tableName, entry.getKey());

            checkedField.assertType(entry.getValue());
            checkedField.assertNullable();
        }

    }

    public static class Datatype extends Model {
        @Column
        int id;

        @Column
        boolean primitiveBoolean;

        @Column
        Boolean wrapperBoolean;

        @Column
        byte primitiveByte;

        @Column
        Byte wrapperByte;

        @Column
        short primitiveShort;

        @Column
        Short wrapperShort;

        @Column
        Integer wrapperInteger;

        @Column
        Long wrapperLong;

        @Column
        float primitiveFloat;

        @Column
        Float wrapperFloat;

        @Column
        double primitiveDouble;

        @Column
        Double wrapperDouble;

        @Column
        char primitiveChar;

        @Column
        String wrapperString;

        @Column
        char[] charArray;

    }
}
