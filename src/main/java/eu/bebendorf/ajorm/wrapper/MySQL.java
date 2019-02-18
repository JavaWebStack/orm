package eu.bebendorf.ajorm.wrapper;

import java.sql.*;

public class MySQL extends BaseSQL {

    private Connection c = null;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;


    public MySQL(String ho, int po, String db, String un, String pw) {
        host=ho;
        port=po;
        database=db;
        username=un;
        password=pw;
    }

    public Connection getConnection(){
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

