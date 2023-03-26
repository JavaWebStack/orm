package org.javawebstack.orm.wrapper;

import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.wrapper.builder.MySQLQueryStringBuilder;
import org.javawebstack.orm.wrapper.builder.QueryStringBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MySQL extends BaseSQL {

    private Connection c = null;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final long timeout;
    private long lastQuery = 0;
    private Map<String, String> customParams = new HashMap<>();

    public MySQL(String host, int port, String database, String username, String password) {
        this(host, port, database, username, password, 60);
    }

    public MySQL(String host, int port, String database, String username, String password, int timeout) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.timeout = timeout * 1000L;
    }

    public MySQL setCustomParam(String key, String value) {
        customParams.put(key, value);
        return this;
    }

    public SQL fork() {
        MySQL sql = new MySQL(host, port, database, username, password, (int) (timeout / 1000L));
        sql.customParams = customParams;
        return sql;
    }

    public Connection getConnection() {
        long now = System.currentTimeMillis();
        if (now > lastQuery + timeout) {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException ignored) {
                }
            }
            c = null;
        }
        lastQuery = now;
        try {
            if (c == null || c.isClosed()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Map<String, String> params = new HashMap<>();
                    params.put("user", this.username);
                    params.put("password", this.password);
                    params.put("autoReconnect", "true");
                    params.put("failOverReadOnly", "false");
                    params.put("maxReconnects", "5");
                    params.put("UseUnicode", "yes");
                    params.put("characterEncoding", "UTF-8");
                    params.putAll(customParams);
                    c = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?" + buildQuery(params));
                } catch (SQLException e) {
                    System.out.println("Error: at getConnection()[MySQL.java]  SQLException   " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    System.out.println("Error: at getConnection()[MySQL.java]  ClassNotFoundException");
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

    private static String buildQuery(Map<String, String> params) {
        return params.entrySet().stream().map(e -> urlEncode(e.getKey()) + "=" + urlEncode(e.getValue())).collect(Collectors.joining("&"));
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return s;
    }

    public QueryStringBuilder builder() {
        return MySQLQueryStringBuilder.INSTANCE;
    }


}

