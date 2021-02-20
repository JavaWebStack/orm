package org.javawebstack.orm.test;

import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.migration.AutoMigrator;
import org.javawebstack.orm.wrapper.MySQL;
import org.javawebstack.orm.wrapper.SQL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class ORMTestCase extends DatabaseConnection {

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
