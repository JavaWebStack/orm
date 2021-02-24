package org.javawebstack.orm.test.automigrate;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.Datatype;
import org.javawebstack.orm.test.shared.models.JustString;
import org.javawebstack.orm.test.shared.verification.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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
    private static final long MAX_SIZE_VARCHAR = (long) Math.floor((65535 - BYTES_OVERHEAD_VARCHAR) / 4);
    private static final long MAX_SIZE_MEDIUMTEXT = (long) Math.floor((16777215 - BYTES_OVERHEAD_MEDIUMTEXT) / 4);
    private static final long MAX_SIZE_LONGTEXT = (long) Math.floor((4294967295L - BYTES_OVERHEAD_LONGTEXT) / 4);

    final static String tableName = "just_strings";

    @Test
    public void testStringUsesDefaultSizeChar() throws ORMConfigurationException, SQLException {
        setUpWithDefaultSize(JustString.class, 1);
        (new Field("just_strings", "string")).assertType("char(1)");
    }

    @Test
    public void testStringUsesDefaultSizeVarchar() throws ORMConfigurationException, SQLException {
        int[] parameters = {2, 3, 123, 1234, 12345, (int) MAX_SIZE_VARCHAR - 1, (int) MAX_SIZE_VARCHAR};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustString.class, singleParameter);
            (new Field("just_strings", "string")).assertType(String.format("varchar(%s)", singleParameter));
        }
    }

    @Test
    public void testStringUsesDefaultSizeMediumText() throws ORMConfigurationException, SQLException {
        int[] parameters = {(int) MAX_SIZE_VARCHAR + 1, 123456, 1234567, (int) MAX_SIZE_MEDIUMTEXT - 1};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustString.class, singleParameter);
            (new Field("just_strings", "string")).assertType("mediumtext");
        }
    }

    @Test
    public void testStringUsesDefaultSizeLongText() throws ORMConfigurationException, SQLException {
        int[] parameters = {(int) MAX_SIZE_MEDIUMTEXT + 1, 123456789, 123467890, Integer.MAX_VALUE};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustString.class, singleParameter);
            (new Field("just_strings", "string")).assertType("longtext");
        }
    }

    private void setUpWithDefaultSize(Class<? extends Model> clazz, int defaultSize) throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(defaultSize);
        ORM.register(clazz, sql(), config);
        ORM.autoMigrate(true);
    }
}
