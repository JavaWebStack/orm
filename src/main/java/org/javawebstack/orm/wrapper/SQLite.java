package org.javawebstack.orm.wrapper;

import org.javawebstack.orm.wrapper.builder.MySQLQueryStringBuilder;
import org.javawebstack.orm.wrapper.builder.QueryStringBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends BaseSQL {

    private Connection c = null;
    private String file;


    public SQLite(String file) {
        this.file = file;
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

    public QueryStringBuilder builder() {
        return MySQLQueryStringBuilder.INSTANCE; // TODO Build a custom one for SQLite
    }


}

