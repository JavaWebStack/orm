package org.javawebstack.orm.connection;

import org.javawebstack.orm.exception.ORMQueryException;

import java.sql.Date;
import java.sql.*;
import java.util.*;

public abstract class BaseSQL implements SQL {

    private final Map<ResultSet, Statement> statementMap = new HashMap<>();
    private final List<QueryLogger> loggers = new LinkedList<>();

    public abstract Connection getConnection();

    public int write(String queryString, Object... parameters) throws SQLException {
        loggers.forEach(l -> l.log(queryString, parameters));
        Connection connection = getConnection();
        if (queryString.toLowerCase(Locale.ROOT).startsWith("insert")) {
            PreparedStatement ps = setParams(connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS), parameters);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
            ps.close();
            return id;
        } else {
            PreparedStatement ps = setParams(connection.prepareStatement(queryString), parameters);
            ps.executeUpdate();
            ps.close();
        }
        return 0;
    }

    public ResultSet read(String queryString, Object... parameters) throws SQLException {
        loggers.forEach(l -> l.log(queryString, parameters));
        Connection connection = getConnection();
        PreparedStatement ps = setParams(connection.prepareStatement(queryString), parameters);
        ResultSet rs = ps.executeQuery();
        statementMap.put(rs, ps);
        return rs;
    }

    private PreparedStatement setParams(PreparedStatement st, Object... parameters) throws SQLException {
        int i = 1;
        for (Object object : parameters) {
            if (object == null) {
                st.setNull(i, Types.NULL);
                i++;
                continue;
            }
            Class<?> type = object.getClass();
            if (type.isEnum())
                st.setString(i, ((Enum<?>) object).name());
            else if (type.equals(String.class))
                st.setString(i, (String) object);
            else if (type.equals(Integer.class))
                st.setInt(i, (int) object);
            else if (type.equals(Double.class))
                st.setDouble(i, (double) object);
            else if (type.equals(Long.class))
                st.setLong(i, (long) object);
            else if (type.equals(Short.class))
                st.setShort(i, (short) object);
            else if (type.equals(Float.class))
                st.setFloat(i, (float) object);
            else if (type.equals(Timestamp.class))
                st.setTimestamp(i, (Timestamp) object);
            else if (type.equals(Date.class))
                st.setDate(i, (Date) object);
            else if (type.equals(Time.class))
                st.setTime(i, (Time) object);
            else if (type.equals(byte[].class))
                st.setBytes(i, (byte[]) object);
            else
                throw new ORMQueryException("Can't set parameter of type: " + object.getClass().getName());
            i++;
        }
        return st;
    }

    public void close(ResultSet rs) {
        if (statementMap.containsKey(rs)) {
            try {
                statementMap.get(rs).close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            statementMap.remove(rs);
        }
        try {
            rs.close();
        } catch (SQLException e) {
            throw new ORMQueryException(e);
        }
    }

    public void cleanUp() {
        for (ResultSet rs : statementMap.keySet())
            close(rs);
    }

    public void addQueryLogger(QueryLogger logger) {
        loggers.add(logger);
    }

    public void removeQueryLogger(QueryLogger logger) {
        loggers.remove(logger);
    }

}
