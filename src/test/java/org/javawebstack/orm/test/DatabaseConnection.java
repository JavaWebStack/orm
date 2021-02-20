package org.javawebstack.orm.test;

import org.javawebstack.orm.wrapper.MySQL;
import org.javawebstack.orm.wrapper.SQL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class DatabaseConnection {

    private static final SQL sql;

    static {
        Properties properties = new Properties();
        File propertiesFile = new File("testenv.properties");
        if(propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch (IOException ignored) {}
        }

        String host = properties.getProperty("mysql.host", System.getenv("MYSQL_HOST"));
        String port = properties.getProperty("mysql.port", System.getenv("MYSQL_PORT"));
        String name = properties.getProperty("mysql.database", System.getenv("MYSQL_DATABASE"));
        String user = properties.getProperty("mysql.username", System.getenv("MYSQL_USERNAME"));
        String password = properties.getProperty("mysql.password", System.getenv("MYSQL_PASSWORD"));

        sql = new MySQL(
                host != null ? host : "localhost",
                port != null ? Integer.parseInt(port) : 3306,
                name != null ? name : "test",
                user != null ? user : "root",
                password != null ? password : ""
        );
    }

    protected static SQL sql() {
        return sql;
    }

}
