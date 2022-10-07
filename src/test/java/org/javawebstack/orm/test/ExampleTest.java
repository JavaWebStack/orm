package org.javawebstack.orm.test;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExampleTest extends ORMTestCase {

    @Test
    public void testExample() throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(Example.class, sql(), config);
        ORM.autoMigrate(true);
        Example example = new Example();
        example.title = "Test";
        example.save();
        example = Repo.get(Example.class).query().first();
        assertNotNull(example);
        assertEquals("Test", example.title);
    }

    public static class Example extends Model {
        @Column
        int id;
        @Column
        String title;
    }

}
