package org.javawebstack.orm.test;

import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.migration.AutoMigrator;
import org.javawebstack.orm.wrapper.MySQL;
import org.javawebstack.orm.wrapper.SQL;

public abstract class ORMTestCase {

    private static final SQL sql;

    static {
        String host = System.getenv("MYSQL_HOST");
        String port = System.getenv("MYSQL_PORT");
        String name = System.getenv("MYSQL_DATABASE");
        String user = System.getenv("MYSQL_USERNAME");
        String password = System.getenv("MYSQL_PASSWORD");
        System.out.println(host);
        System.out.println(port);
        System.out.println(name);
        System.out.println(user);
        System.out.println(password);
        sql = new MySQL(
                host != null ? host : "localhost",
                port != null ? Integer.parseInt(port) : 3306,
                name != null ? name : "test",
                user != null ? user : "root",
                password != null ? password : ""
        );
    }

    {
        reset();
    }

    protected static void reset() {
        AutoMigrator.drop(ORM.getRepos().toArray(new Repo[0]));
        ORM.reset();
    }

    protected static SQL sql() {
        return sql;
    }

}
