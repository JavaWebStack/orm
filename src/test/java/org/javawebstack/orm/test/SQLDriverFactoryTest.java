package org.javawebstack.orm.test;

import org.javawebstack.orm.connection.MySQL;
import org.javawebstack.orm.util.SQLDriverFactory;
import org.javawebstack.orm.util.SQLDriverNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SQLDriverFactoryTest {

    private SQLDriverFactory factory;
    private final Map<String, String > configMap = new HashMap<String, String >(){
        {
            put("file", "sb.sqlite");
            put("host", "localhost");
            put("port", "3306");
            put("name", "app");
            put("user", "root");
            put("password", "");
        }
    };


    @BeforeEach
    public void beforeEach() {
        factory = new SQLDriverFactory(configMap);
    }

    /*
     * Normal Cases
     */
    @Test
    public void testSQLite() throws SQLDriverNotFoundException {
        assertNotNull(factory.getDriver("sqlite"));
    }

    @Test
    public void testMySQL() throws SQLDriverNotFoundException {
        assertNotNull(factory.getDriver("mysql"));
    }

    @Test
    public void testRegisrtration() throws SQLDriverNotFoundException {
        factory.registerDriver("mariadb", () -> new MySQL(
                configMap.get("host"),
                Integer.parseInt(configMap.get("port")),
                configMap.get("name"),
                configMap.get("user"),
                configMap.get("password")
        ));

        assertNotNull(factory.getDriver("mariadb"));
    }

    /*
     * Edge Cases
     */

    @Test
    public void testDriverNameIsCaseSensitive() {
        assertThrows(
                SQLDriverNotFoundException.class,
                () -> factory.getDriver("MySQL")
        );
    }

    /*
     * Error Cases
     */

    @Test
    public void testCannotFindUninitializedDriver() {
        assertThrows(
                SQLDriverNotFoundException.class,
                () -> factory.getDriver("nodriver")
        );
    }
}