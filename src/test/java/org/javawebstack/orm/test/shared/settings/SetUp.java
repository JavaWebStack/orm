package org.javawebstack.orm.test.shared.settings;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.exception.ORMConfigurationException;

/**
 * This class should unify the set ups used for the tests as they are very similar to each other.
 */
public class SetUp extends MySQLConnectionContainer {

    public static void setUpWithDefaultSize(Class<? extends Model> clazz, int defaultSize) throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(defaultSize);
        ORM.register(clazz, sql(), config);
        ORM.autoMigrate(true);
    }
}
