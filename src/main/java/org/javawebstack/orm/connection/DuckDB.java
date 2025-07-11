package org.javawebstack.orm.connection;

import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.renderer.MySQLQueryStringRenderer;
import org.javawebstack.orm.renderer.QueryStringRenderer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class DuckDB extends BaseSQL {

    private Connection c = null;
    private final String path;
    private final boolean readOnly;
    private Map<String, String> customParams = new HashMap<>();

    public DuckDB() {
        this(null);
    }

    public DuckDB(String path) {
        this(path, false);
    }

    public DuckDB(String path, boolean readOnly) {
        this.path = path;
        this.readOnly = readOnly;
    }

    public DuckDB setCustomParam(String key, String value) {
        customParams.put(key, value);
        return this;
    }

    public SQL fork() {
        if(!readOnly) {
            throw new ORMQueryException("Forking is only supported for read-only connections");
        }
        DuckDB sql = new DuckDB(path, readOnly);
        sql.customParams = customParams;
        return sql;
    }

    public Connection getConnection() {
        try {
            if (c == null || c.isClosed()) {
                try {
                    Class.forName("org.duckdb.DuckDBDriver");
                    Properties params = new Properties();
                    params.put("duckdb.read_only", this.readOnly ? "true" : "false");
                    params.putAll(customParams);
                    c = DriverManager.getConnection("jdbc:duckdb:" + (this.path != null ? this.path : ""), params);
                } catch (SQLException e) {
                    System.out.println("Error: at getConnection()[DuckDB.java]  SQLException   " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    System.out.println("Error: at getConnection()[DuckDB.java]  ClassNotFoundException");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (c == null || c.isClosed())
                throw new ORMQueryException("Connection failed!");
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
        return MySQLQueryStringRenderer.INSTANCE;
    }

}

