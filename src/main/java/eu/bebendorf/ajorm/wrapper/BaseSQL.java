package eu.bebendorf.ajorm.wrapper;

import eu.bebendorf.ajorm.SQL;

import java.sql.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class BaseSQL implements SQL {

    private Map<ResultSet,Statement> statementMap = new HashMap<>();

    public abstract Connection getConnection();

    public int write(String queryString,Object... parameters){
        try {
            if(queryString.toLowerCase(Locale.ROOT).startsWith("insert")){
                PreparedStatement ps = setParams(getConnection().prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS),parameters);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                int id = 0;
                if(rs.next()){
                    id = rs.getInt(1);
                }
                rs.close();
                ps.close();
                return id;
            }else{
                PreparedStatement ps = setParams(getConnection().prepareStatement(queryString),parameters);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {e.printStackTrace();}
        return 0;
    }

    public ResultSet read(String queryString, Object... parameters){
        try {
            PreparedStatement ps = setParams(getConnection().prepareStatement(queryString),parameters);
            ResultSet rs = ps.executeQuery();
            statementMap.put(rs,ps);
            return rs;
        } catch (SQLException e) {e.printStackTrace();}
        return null;
    }

    private PreparedStatement setParams(PreparedStatement st, Object... parameters) throws SQLException {
        int i = 1;
        for(Object object : parameters){
            if(object == null)
                st.setObject(i, null);
            Class type = object.getClass();
            if(type.isEnum())
                st.setString(i,((Enum) object).name());
            else if(type.equals(String.class))
                st.setString(i,(String)object);
            else if(type.equals(int.class)||type.equals(Integer.class))
                st.setInt(i,(int)object);
            else if(type.equals(double.class)||type.equals(Double.class))
                st.setDouble(i,(double)object);
            else if(type.equals(long.class)||type.equals(Long.class))
                st.setLong(i,(long)object);
            else if(type.equals(short.class)||type.equals(Short.class))
                st.setShort(i,(short)object);
            else if(type.equals(float.class)||type.equals(Float.class))
                st.setFloat(i,(float)object);
            else if(type.equals(Timestamp.class))
                st.setTimestamp(i,(Timestamp) object);
            else if(type.equals(Date.class))
                st.setDate(i,(Date)object);
            else if(type.equals(Time.class))
                st.setTime(i,(Time)object);
            else
                System.out.println("[SQL] Could not set type: "+object.getClass().getName());
            i++;
        }
        return st;
    }

    public void close(ResultSet rs){
        if(statementMap.containsKey(rs)){
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
            e.printStackTrace();
        }
    }

    public void cleanUp(){
        for(ResultSet rs : statementMap.keySet())
            close(rs);
    }

}
