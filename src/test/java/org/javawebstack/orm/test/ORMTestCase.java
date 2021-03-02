package org.javawebstack.orm.test;

import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.migration.AutoMigrator;
import org.javawebstack.orm.test.shared.settings.MySQLConnectionContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class ORMTestCase extends MySQLConnectionContainer {

    @BeforeEach
    public void beforeEach() {
        reset();
    }

    @AfterAll
    public static void finishTestClass() { reset(); }

    public static void reset() {
        AutoMigrator.drop(ORM.getRepos().toArray(new Repo[0]));
        ORM.reset();
    }

}
