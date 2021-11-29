package org.javawebstack.orm.test;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.util.KeyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TableInfoTest extends ORMTestCase {
    @Test
    public void testParentTableInfo () throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(Child.class, sql(), config);

        assertEquals(Repo.get(Child.class).getInfo().getFields().size(), 2);
    }

    @Test
    public void testParentTableMigration () throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(Child.class, sql(), config);
        ORM.autoMigrate(true);

        Child child = new Child();
        child.id = 1337;
        child.content = "Hello World!";
        child.save();

        Child fetchedChild = Repo.get(Child.class).get(1337);
        assertEquals(fetchedChild.id, child.id);
        assertEquals(fetchedChild.content, child.content);
    }


    public static abstract class Parent extends Model {
        @Column(ai = true, key = KeyType.PRIMARY)
        public int id;
    }

    public static class Child extends Parent {
        @Column
        public String content;
    }
}
