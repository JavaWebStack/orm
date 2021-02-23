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

    final static String tableName = "just_strings";


    @Test
    public void testStringUsesDefaultSize() throws ORMConfigurationException, SQLException {
        int[] parameters = {2, 3, 100, 254, 255, 256, 65534, 65535};
        for( int singleParameter : parameters) {
            setUpWithDefaultSize(JustString.class, singleParameter);
            (new Field("just_strings", "string")).assertType(String.format("varchar(%s)", singleParameter));
        }
    }

    private void setUpWithDefaultSize(Class<? extends Model> clazz, int defaultSize) throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(defaultSize);
        ORM.register(clazz, sql(), config);
        ORM.autoMigrate(true);
    }
}
