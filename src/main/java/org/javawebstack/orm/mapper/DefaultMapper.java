package org.javawebstack.orm.mapper;

import org.javawebstack.orm.SQLType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public Class<?> getInternalType(Class<?> type){
        if(type.equals(String.class))
            return String.class;
        if(type.equals(UUID.class))
            return String.class;
        if(type.isEnum())
            return String.class;
        if(type.equals(Boolean.class) || type.equals(boolean.class))
            return Integer.class;
        if(type.equals(int.class) || type.equals(Integer.class))
            return Integer.class;
        if(type.equals(double.class) || type.equals(Double.class))
            return Double.class;
        if(type.equals(float.class) || type.equals(Float.class))
            return Float.class;
        if(type.equals(long.class) || type.equals(Long.class))
            return Long.class;
        if(type.equals(Date.class) || type.equals(Timestamp.class))
            return Timestamp.class;
        return null;
    }

}
