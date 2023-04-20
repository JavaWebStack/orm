package org.javawebstack.orm;

import org.javawebstack.orm.wrapper.SQL;

import java.util.function.Consumer;

public class Session implements AutoCloseable {

    private static ThreadLocal<Session> sessions = new ThreadLocal<>();

    public static Session current() {
        return sessions.get();
    }

    private SQL connection;

    private Session() {

    }

    public Session via(SQL connection) {
        this.connection = connection;
        return this;
    }

    public SQL getConnection() {
        return connection;
    }

    public static void session(Consumer<Session> consumer) {
        Session session = begin();
        consumer.accept(session);
        end();
    }

    public static Session begin() {
        Session session = new Session();
        sessions.set(session);
        return session;
    }

    public static void end() {
        sessions.remove();
    }

    public void close() throws Exception {
        end();
    }
}
