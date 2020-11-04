package org.javawebstack.orm.mapper;

import org.javawebstack.orm.SQLType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class DefaultMapper implements TypeMapper {

    public Object mapToSQL(Object source, Class<?> type) {
        if(source == null)
            return null;
        if(source instanceof Date)
            return Timestamp.from(((Date) source).toInstant());
        if(type.isEnum())
            return ((Enum<?>)source).name();
        if(type.equals(Boolean.class))
            return Integer.valueOf(((Boolean)source)?1:0);
        if(type.equals(boolean.class))
            return Integer.valueOf(((boolean)source)?1:0);
        if(type.equals(int.class))
            return Integer.valueOf((int)source);
        if(type.equals(long.class))
            return Long.valueOf((long)source);
        if(type.equals(double.class))
            return Double.valueOf((double)source);
        if(type.equals(float.class))
            return Float.valueOf((float)source);
        if(type.equals(UUID.class))
            return source.toString();
        return source;
    }

    public Object mapToJava(Object source, Class<?> type) {
        if(source == null)
            return null;
        if(type.isEnum())
            return Enum.valueOf((Class<Enum>) type, (String) source);
        if(type.equals(UUID.class))
            return UUID.fromString((String) source);
        if(type.equals(Date.class))
            return Date.from(((Timestamp)source).toInstant());
        if(type.equals(Boolean.class) || type.equals(boolean.class))
            return ((Integer) source) == 1;
        if(type.equals(long.class))
            return ((Long) source).longValue();
        if(type.equals(int.class))
            return ((Integer) source).intValue();
        if(type.equals(double.class))
            return ((Double) source).doubleValue();
        if(type.equals(float.class))
            return ((Float) source).floatValue();
        return source;
    }

    public SQLType getType(Class<?> type, int size){
        if(type.equals(String.class))
            return size > 255 || size < 1 ? SQLType.TEXT : SQLType.VARCHAR;
        if(type.equals(UUID.class))
            return SQLType.VARCHAR;
        if(type.isEnum())
            return SQLType.ENUM;
        if(type.equals(Boolean.class) || type.equals(boolean.class))
            return SQLType.TINYINT;
        if(type.equals(int.class) || type.equals(Integer.class))
            return SQLType.INT;
        if(type.equals(double.class) || type.equals(Double.class))
            return SQLType.DOUBLE;
        if(type.equals(float.class) || type.equals(Float.class))
            return SQLType.FLOAT;
        if(type.equals(long.class) || type.equals(Long.class))
            return SQLType.BIGINT;
        if(type.equals(Date.class) || type.equals(Timestamp.class))
            return SQLType.TIMESTAMP;
        return null;
    }

    public String getTypeParameters(Class<?> type, int size){
        if(type.equals(String.class))
            return size > 255 || size < 1 ? null : String.valueOf(size);
        return null;
    }

}
