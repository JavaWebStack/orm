package eu.bebendorf.ajorm.mapper;

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

    public Class<?> getTargetType(Class<?> type){
        if(type.equals(UUID.class))
            return String.class;
        if(type.equals(Boolean.class) || type.equals(boolean.class))
            return Integer.class;
        if(type.equals(int.class))
            return Integer.class;
        if(type.equals(double.class))
            return Double.class;
        if(type.equals(float.class))
            return Float.class;
        if(type.equals(long.class))
            return Long.class;
        if(type.equals(Date.class))
            return Timestamp.class;
        return type;
    }

    public String getSQLType(Class<?> type, int size){
        if(type.equals(String.class))
            return size>0?"VARCHAR("+size+")":"TEXT";
        if(type.equals(Integer.class) || type.equals(int.class))
            return "INT";
        if(type.equals(Long.class) || type.equals(long.class))
            return "BIGINT";
        if(type.equals(Double.class) || type.equals(double.class))
            return "DOUBLE";
        if(type.equals(Float.class) || type.equals(float.class))
            return "FLOAT";
        if(type.equals(Timestamp.class))
            return "TIMESTAMP";
        if(type.equals(Date.class))
            return getSQLType(Timestamp.class, size);
        if(type.equals(Boolean.class) || type.equals(boolean.class))
            return "TINYINT";
        if(type.equals(UUID.class))
            return getSQLType(String.class, 36);
        if(type.isEnum()){
            List<String> values = new ArrayList<>();
            for(Object v : type.getEnumConstants()){
                Enum<?> vObject = (Enum<?>) v;
                values.add("'"+vObject.name()+"'");
            }
            return "ENUM("+String.join(",", values)+")";
        }
        return null;
    }

}
