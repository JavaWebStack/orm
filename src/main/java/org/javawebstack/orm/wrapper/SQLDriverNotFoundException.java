package org.javawebstack.orm.wrapper;

public class SQLDriverNotFoundException extends Exception {
    private String name;

    public SQLDriverNotFoundException (String name) {
        this.name = name;
    }


    public String getMessage() {
        return "SQL Driver " + name + " not found!";
    }
}
