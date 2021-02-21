package org.javawebstack.orm.test.automigrate;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.verification.Field;
import org.javawebstack.orm.test.shared.verification.IdField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public void testId() throws SQLException {
        IdField.assertCorrectDatabaseFormat(tableName);
    }

    @Test
    public void testPrimitiveIntegerDatatype() throws SQLException {
        Field checkedField;

        Map<String, String> columnDataTypeMap = new HashMap<>();

        columnDataTypeMap.put("primitive_boolean", "tinyint(1)");
        columnDataTypeMap.put("wrapper_boolean", "tinyint(1)");

        columnDataTypeMap.put("primitive_byte", "tinyint(4)");
        columnDataTypeMap.put("wrapper_byte", "tinyint(4)");

        columnDataTypeMap.put("primitive_short", "smallint(6)");
        columnDataTypeMap.put("wrapper_short", "smallint(6)");

        columnDataTypeMap.put("primitive_integer", "int(11)");
        columnDataTypeMap.put("wrapper_integer", "int(11)");

        columnDataTypeMap.put("primitive_long", "bigint(20)");
        columnDataTypeMap.put("wrapper_long", "bigint(20)");

        columnDataTypeMap.put("primitive_float", "float");
        columnDataTypeMap.put("wrapper_float", "float");

        columnDataTypeMap.put("primitive_double", "double");
        columnDataTypeMap.put("wrapper_double", "double");

        columnDataTypeMap.put("primitive_char", "varchar(1)");

        columnDataTypeMap.put("wrapper_string", "varchar(255)");

        columnDataTypeMap.put("char_array", "varchar(255)");

        columnDataTypeMap.put("byte_array", "varbinary(255)");

        columnDataTypeMap.put("timestamp", "timestamp(6)");

        columnDataTypeMap.put("date", "date");

        columnDataTypeMap.put("uuid", "varchar(36)");

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
        int primitiveInteger;

        @Column
        Integer wrapperInteger;

        @Column
        long primitiveLong;

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

        @Column
        byte[] byteArray;

        @Column
        Timestamp timestamp;

        @Column
        Date date;

        @Column
        UUID uuid;
    }
}
