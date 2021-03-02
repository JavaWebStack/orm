package org.javawebstack.orm.test;

import static org.junit.jupiter.api.Assertions.*;

import org.javawebstack.orm.wrapper.SQLDriverFactory;
import org.javawebstack.orm.wrapper.SQLDriverNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class SQLDriverFactoryTest {
    private SQLDriverFactory factory = new SQLDriverFactory(new HashMap<String, String>() {{
        put("file", "sb.sqlite");
        put("host", "localhost");
        put("port", "3306");
        put("name", "app");
        put("user", "root");
        put("password", "");
    }});

    @Test
    public void testSQLite() throws SQLDriverNotFoundException {
        assertNotNull(factory.getDriver("sqlite"));
    }

    @Test
    public void testMySQL() throws SQLDriverNotFoundException {
        assertNotNull(factory.getDriver("mysql"));
    }
}