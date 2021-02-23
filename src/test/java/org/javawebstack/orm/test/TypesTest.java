package org.javawebstack.orm.test;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TypesTest extends ORMTestCase {

    @Test
    public void testFields() throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(ExampleModel.class, sql(), config);
        ORM.autoMigrate(true);

        Timestamp timestamp = Timestamp.from(Instant.now());
        ExampleModel model   = new ExampleModel();
        model.exampleString  = "Hello ;)";
        model.exampleEnum    = ExampleModel.Type.USER;

        model.exampleFloatPrimitive   = 1.33769F;
        model.exampleFloat   = 1.33769F;

        model.exampleDoublePrimitive  = 123456789.1234567890D;
        model.exampleDouble  = 123456789.1234567890D;

        model.exampleLongPrimitive    = 999999999999999999L;
        model.exampleLong    = 999999999999999999L;

        model.exampleBooleanPrimitive = true;
        model.exampleBoolean = true;

        model.timestampTest  = timestamp;
        model.save();

        int id = model.id;

        model = Repo.get(ExampleModel.class).get(id);

        assertEquals(model.id, id);
        assertNull(model.exampleNull);
        assertTrue(model.exampleBoolean);
        assertTrue(model.exampleBooleanPrimitive);
        assertEquals(model.exampleEnum, ExampleModel.Type.USER);
        assertEquals(model.exampleString, "Hello ;)");
        assertEquals(model.exampleFloatPrimitive, 1.33769f);
        assertEquals(model.exampleFloat, 1.33769f);
        assertEquals(model.exampleDoublePrimitive, 123456789.1234567890D);
        assertEquals(model.exampleDouble, 123456789.1234567890D);
        assertEquals(model.exampleLong, 999999999999999999L);
        assertEquals(model.exampleLongPrimitive, 999999999999999999L);

        assertEquals(timestamp.getTime() / 1000, model.timestampTest.getTime() / 1000);

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
        public float exampleFloatPrimitive;

        @Column
        public Float exampleFloat;

        @Column
        public double exampleDoublePrimitive;

        @Column
        public Double exampleDouble;

        @Column
        public long exampleLongPrimitive;

        @Column
        public Long exampleLong;

        @Column
        public boolean exampleBooleanPrimitive;

        @Column
        public Boolean exampleBoolean;

        @Column
        public Type exampleEnum;

        @Column
        public String exampleNull;

        @Column
        public Timestamp timestampTest;

        public enum Type {
            ADMIN, USER, GUEST
        }

    }

}
