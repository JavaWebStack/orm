package org.javawebstack.orm.connection;

import org.javawebstack.orm.renderer.MySQLQueryStringRenderer;
import org.javawebstack.orm.renderer.QueryStringRenderer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends BaseSQL {

    private Connection c = null;
    private String file;


    public SQLite(String file) {
        this.file = file;
    }

    public SQL fork() {
        return new SQLite(file);
    }

    public Connection getConnection() {
        try {
            if (c == null || c.isClosed()) {
                try {
                    c = DriverManager.getConnection("jdbc:sqlite:" + file);
                } catch (SQLException e) {
                    System.out.println("Error: at getConnection()[SQLite.java]  SQLException   " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (c != null && c.isClosed())
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public void close() {
        if(c != null) {
            try {
                if(!c.isClosed())
                    c.close();
            } catch (SQLException ignored) {}
            c = null;
        }
    }

    public QueryStringRenderer builder() {
        return MySQLQueryStringRenderer.INSTANCE; // TODO Build a custom one for SQLite
    }


}

