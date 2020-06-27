package eu.bebendorf.ajorm.wrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MySQL extends BaseSQL {

    private Connection c = null;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final long timeout;
    private long lastQuery = 0;

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

    public Connection getConnection(){
        long now = System.currentTimeMillis();
        if(now > lastQuery+timeout){
            try {
                c.close();
            } catch (SQLException throwables) {}
            c = null;
        }
        lastQuery = now;
        try {
            if(c==null||c.isClosed()){
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    c = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?user=" + this.username + "&password=" + this.password + "&autoReconnect=" + true + "&failOverReadOnly=false&maxReconnects=" + 5 + "&UseUnicode=yes&characterEncoding=UTF-8");
                } catch (SQLException e) {
                    System.out.println("Fehler: bei getConnection()[MySQL.java]  SQLException   " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    System.out.println("Fehler: bei getConnection()[MySQL.java]  ClassNotFoundException");
                }
            }
        } catch (SQLException e) {e.printStackTrace();}
        try {
            if(c!=null&&c.isClosed())
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }



}

