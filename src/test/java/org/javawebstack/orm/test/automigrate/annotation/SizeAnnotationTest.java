package org.javawebstack.orm.test.automigrate.annotation;

import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.SizableDatatypesOfSizeThree;
import org.javawebstack.orm.test.shared.verification.Field;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.javawebstack.orm.test.shared.settings.SetUp.setUpWithDefaultSize;

class SizeAnnotationTest extends ORMTestCase {

    private static final String tableName = "sizable_datatypes_of_size_threes";

    private final Map<String, String> columnDataTypeMap;

    {
        columnDataTypeMap = new HashMap<>();

        columnDataTypeMap.put("primitive_byte", "tinyint(3)");
        columnDataTypeMap.put("wrapper_byte", "tinyint(3)");

        columnDataTypeMap.put("primitive_short", "smallint(3)");
        columnDataTypeMap.put("wrapper_short", "smallint(3)");

        columnDataTypeMap.put("primitive_integer", "int(3)");
        columnDataTypeMap.put("wrapper_integer", "int(3)");

        columnDataTypeMap.put("primitive_long", "bigint(3)");
        columnDataTypeMap.put("wrapper_long", "bigint(3)");

        columnDataTypeMap.put("wrapper_string", "varchar(3)");
        columnDataTypeMap.put("char_array", "varchar(3)");

        columnDataTypeMap.put("byte_array", "varbinary(3)");

        columnDataTypeMap.put("uuid", "varchar(3)");
    }

    @Test
    void testSizeSpecificationOverwritesDefaultSizeString() throws ORMConfigurationException, SQLException {
        setUpWithDefaultSize(SizableDatatypesOfSizeThree.class, 255);
        Field checkedField;

        for(Map.Entry<String, String> entry : columnDataTypeMap.entrySet()) {
            checkedField = new Field(tableName, entry.getKey());
            checkedField.assertType(entry.getValue());
        }
    }

}
