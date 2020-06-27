package eu.bebendorf.ajorm.wrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MySQL extends BaseSQL {

    private Connection c = null;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private Executor timeoutExecutor;
    private int timeout;

    public MySQL(String host, int port, String database, String username, String password) {
        this(host, port, database, username, password, 60);
    }

    public MySQL(String host, int port, String database, String username, String password, int timeout) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        timeoutExecutor = Executors.newSingleThreadExecutor();
    }

    public Connection getConnection(){
        try {
            if(c==null||c.isClosed()){
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    c = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?user=" + this.username + "&password=" + this.password + "&autoReconnect=" + true + "&failOverReadOnly=false&maxReconnects=" + 5 + "&UseUnicode=yes&characterEncoding=UTF-8");
                    c.setNetworkTimeout(timeoutExecutor, timeout);
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

