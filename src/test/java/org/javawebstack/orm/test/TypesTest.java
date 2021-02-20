package org.javawebstack.orm.test;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TypesTest extends ExampleTest {

    @Test
    public void testFields() throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(ExampleModel.class, sql(), config);
        ORM.autoMigrate(true);

        ExampleModel model   = new ExampleModel();
        model.exampleString  = "Hello ;)";
        model.exampleEnum    = ExampleModel.Type.USER;
        model.exampleFloat   = 1.33769F;
        model.exampleDouble  = 123456789.1234567890D;

        model.exampleLong    = 999999999999999999L;
        model.exampleBoolean = true;
        model.save();

        int id = model.id;

        model = Repo.get(ExampleModel.class).get(id);

        assertEquals(model.id, id);
        assertNull(model.exampleNull);
        assertTrue(model.exampleBoolean);
        assertEquals(model.exampleEnum, ExampleModel.Type.USER);
        assertEquals(model.exampleString, "Hello ;)");
        assertEquals(model.exampleFloat, 1.33769f);
        assertEquals(model.exampleDouble, 123456789.1234567890D);
        assertEquals(model.exampleLong, 999999999999999999L);

        model.exampleNull = "Text";
        model.save();
        model = Repo.get(ExampleModel.class).get(id);
        assertEquals(model.exampleNull, "Text");
    }

    public static class ExampleModel extends Model {

        @Column
        public int id;

        @Column
        public String exampleString;

        @Column
        public float exampleFloat;

        @Column
        public double exampleDouble;

        @Column
        public long exampleLong;

        @Column
        public boolean exampleBoolean;

        @Column
        public Type exampleEnum;

        @Column
        public String exampleNull;

        public enum Type {
            ADMIN, USER, GUEST
        }

    }

}
