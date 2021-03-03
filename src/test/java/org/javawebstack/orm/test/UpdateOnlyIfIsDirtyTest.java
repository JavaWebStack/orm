package org.javawebstack.orm.test;

import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.shared.models.JustString;
import org.javawebstack.orm.wrapper.QueryLogger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateOnlyIfIsDirtyTest extends ORMTestCase {

    @Test
    public void testOnlyUpdateIfIsDirty() throws ORMConfigurationException {
        boolean[] updated = new boolean[1];
        QueryLogger logger = (query, parameters) -> {
            if(query.startsWith("UPDATE"))
                updated[0] = true;
        };
        sql().addQueryLogger(logger);
        ORM.register(JustString.class, sql(), new ORMConfig());
        ORM.autoMigrate();
        JustString model = new JustString();
        model.setString("Test");
        model.save();
        model.save();
        assertFalse(updated[0]);
        model.setString("TestB");
        model.save();
        assertTrue(updated[0]);
        sql().removeQueryLogger(logger);
    }

    @Test
    public void testPreventUnnecessaryUpdatesOption() throws ORMConfigurationException {
        boolean[] updated = new boolean[1];
        QueryLogger logger = (query, parameters) -> {
            if(query.startsWith("UPDATE"))
                updated[0] = true;
        };
        sql().addQueryLogger(logger);
        ORM.register(JustString.class, sql(), new ORMConfig().setPreventUnnecessaryUpdates(false));
        ORM.autoMigrate();
        JustString model = new JustString();
        model.setString("Test");
        model.save();
        model.save();
        assertTrue(updated[0]);
        sql().removeQueryLogger(logger);
    }

}
