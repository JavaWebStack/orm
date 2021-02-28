package org.javawebstack.orm.test.automigrate;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.Datatype;
import org.javawebstack.orm.test.shared.models.JustCharArray;
import org.javawebstack.orm.test.shared.models.JustString;
import org.javawebstack.orm.test.shared.verification.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * This class tests if the default size is being applied for the types:
 * - String
 * - char[]
 *
 * This class also tests that the default size is not applied to the type:
 * - short
 * - Short
 * - int
 * - Integer
 * - float
 * - Float
 * - double
 * - Double
 */
public class DefaultSizeTest extends ORMTestCase {

    // Copy from DefaultMapper:
    // SOURCES:
    // TEXT Datatypes: https://www.mysqltutorial.org/mysql-text/
    private static final int BYTES_OVERHEAD_VARCHAR = 4;
    private static final int BYTES_OVERHEAD_TINYTEXT = 1;
    private static final int BYTES_OVERHEAD_TEXT = 2;
    private static final int BYTES_OVERHEAD_MEDIUMTEXT = 3;
    private static final int BYTES_OVERHEAD_LONGTEXT = 4;

    // The max sizes given in the manual are in bytes. There are overheads which need to be subtracted.
    // The following values assume utf8mb4 encoding which uses 4 bytes per character and
    // further quarters the maximum column length accordingly.
    private static final long MAX_SIZE_VARCHAR = (long) Math.floor((65535 - BYTES_OVERHEAD_VARCHAR) / 4.0);
    private static final long MAX_SIZE_MEDIUMTEXT = (long) Math.floor((16777215 - BYTES_OVERHEAD_MEDIUMTEXT) / 4.0);
    private static final long MAX_SIZE_LONGTEXT = (long) Math.floor((4294967295L - BYTES_OVERHEAD_LONGTEXT) / 4.0);

    final static String tableNameString = "just_strings";
    final static String columnNameString = "string";

    // Not renaming the table name as this is not focus of the test
    final static String tableNameCharArray = "just_char_arraies";
    final static String columnNameCharArray = "char_array";

    final static String tableNameDatatype = "datatypes";

    @Test
    public void testStringUsesDefaultSizeChar() throws ORMConfigurationException, SQLException {
        setUpWithDefaultSize(JustString.class, 1);
        (new Field(tableNameString, columnNameString)).assertType("char(1)");
    }

    @Test
    public void testStringUsesDefaultSizeVarchar() throws ORMConfigurationException, SQLException {
        int[] parameters = {2, 3, 123, 1234, 12345, (int) MAX_SIZE_VARCHAR - 1, (int) MAX_SIZE_VARCHAR};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustString.class, singleParameter);
            (new Field(tableNameString, columnNameString)).assertType(String.format("varchar(%s)", singleParameter));
        }
    }

    @Test
    public void testStringUsesDefaultSizeMediumText() throws ORMConfigurationException, SQLException {
        int[] parameters = {(int) MAX_SIZE_VARCHAR + 1, 123456, 1234567, (int) MAX_SIZE_MEDIUMTEXT - 1};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustString.class, singleParameter);
            (new Field(tableNameString, columnNameString)).assertType("mediumtext");
        }
    }

    @Test
    public void testStringUsesDefaultSizeLongText() throws ORMConfigurationException, SQLException {
        int[] parameters = {(int) MAX_SIZE_MEDIUMTEXT + 1, 123456789, 123467890, Integer.MAX_VALUE};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustString.class, singleParameter);
            (new Field(tableNameString, columnNameString)).assertType("longtext");
        }
    }

    @Test
    public void testCharArrayUsesDefaultSizeChar() throws ORMConfigurationException, SQLException {
        setUpWithDefaultSize(JustCharArray.class, 1);
        (new Field(tableNameCharArray, columnNameCharArray)).assertType("char(1)");
    }

    @Test
    public void testCharArrayUsesDefaultSizeVarchar() throws ORMConfigurationException, SQLException {
        int[] parameters = {2, 3, 123, 1234, 12345, (int) MAX_SIZE_VARCHAR - 1, (int) MAX_SIZE_VARCHAR};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustCharArray.class, singleParameter);
            (new Field(tableNameCharArray, columnNameCharArray)).assertType(String.format("varchar(%s)", singleParameter));
        }
    }

    @Test
    public void testCharArrayUsesDefaultSizeMediumText() throws ORMConfigurationException, SQLException {
        int[] parameters = {(int) MAX_SIZE_VARCHAR + 1, 123456, 1234567, (int) MAX_SIZE_MEDIUMTEXT - 1};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustCharArray.class, singleParameter);
            (new Field(tableNameCharArray, columnNameCharArray)).assertType("mediumtext");
        }
    }

    @Test
    public void testCharArrayUsesDefaultSizeLongText() throws ORMConfigurationException, SQLException {
        int[] parameters = {(int) MAX_SIZE_MEDIUMTEXT + 1, 123456789, 123467890, Integer.MAX_VALUE};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustCharArray.class, singleParameter);
            (new Field(tableNameCharArray, columnNameCharArray)).assertType("longtext");
        }
    }

    @Test
    public void testOtherDataTypesDoNotUseDefaultSize() throws ORMConfigurationException, SQLException {
        // smallint defaults to the size 6 the default size should therefore not be chosen as 6 or higher;
        setUpWithDefaultSize(Datatype.class, 5);

        // smallint defaults to 6
        (new Field(tableNameDatatype, "primitive_short")).assertType("smallint(6)");
        (new Field(tableNameDatatype, "wrapper_short")).assertType("smallint(6)");

        // tinyint defaults to 11
        (new Field(tableNameDatatype, "primitive_integer")).assertType("int(11)");
        (new Field(tableNameDatatype, "wrapper_integer")).assertType("int(11)");

        // bigint defaults to 20
        (new Field(tableNameDatatype, "primitive_long")).assertType("bigint(20)");
        (new Field(tableNameDatatype, "wrapper_long")).assertType("bigint(20)");

        (new Field(tableNameDatatype, "primitive_float")).assertType("float");
        (new Field(tableNameDatatype, "wrapper_float")).assertType("float");

        (new Field(tableNameDatatype, "primitive_double")).assertType("double");
        (new Field(tableNameDatatype, "wrapper_double")).assertType("double");
    }

    private void setUpWithDefaultSize(Class<? extends Model> clazz, int defaultSize) throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(defaultSize);
        ORM.register(clazz, sql(), config);
        ORM.autoMigrate(true);
    }
}
