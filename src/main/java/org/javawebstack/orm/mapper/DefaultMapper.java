package org.javawebstack.orm.mapper;

import org.javawebstack.orm.SQLType;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultMapper implements TypeMapper {

    public Object mapToSQL(Object source, Class<?> type) {
        if (source == null)
            return null;
        if (type.isEnum())
            return ((Enum<?>) source).name();
        if (type.equals(Boolean.class))
            return Integer.valueOf(((Boolean) source) ? 1 : 0);
        if (type.equals(boolean.class))
            return Integer.valueOf(((boolean) source) ? 1 : 0);
        if (type.equals(byte.class))
            return Byte.valueOf((byte) source);
        if (type.equals(short.class))
            return Short.valueOf((short) source);
        if (type.equals(int.class))
            return Integer.valueOf((int) source);
        if (type.equals(long.class))
            return Long.valueOf((long) source);
        if (type.equals(double.class))
            return Double.valueOf((double) source);
        if (type.equals(float.class))
            return Float.valueOf((float) source);
        if (type.equals(char[].class))
            return String.valueOf((char[]) source);
        if (type.equals(UUID.class))
            return source.toString();
        if (type.equals(Timestamp.class))
            return ((Timestamp) source).toString();
        return source;
    }

    public Object mapToJava(Object source, Class<?> type) {
        if (source == null)
            return null;
        if (type.isEnum())
            return Enum.valueOf((Class<Enum>) type, (String) source);
        if (type.equals(UUID.class))
            return UUID.fromString((String) source);
        if (type.equals(boolean.class))
            return ((Boolean) source).booleanValue();
        if (type.equals(byte.class))
            return ((Byte) source).byteValue();
        if (type.equals(short.class))
            return ((Short) source).shortValue();
        if (type.equals(int.class))
            return ((Integer) source).intValue();
        if (type.equals(long.class))
            return ((Long) source).longValue();
        if (type.equals(double.class))
            return ((Double) source).doubleValue();
        if (type.equals(float.class))
            return ((Float) source).floatValue();
        if (type.equals(char[].class))
            return ((String) source).toCharArray();
        return source;
    }

    public SQLType getType(Class<?> type, int size) {
        if (type.equals(String.class) || type.equals(char[].class))
            return size > 65535 || size < 1 ? SQLType.TEXT : SQLType.VARCHAR;
        if (type.equals(UUID.class))
            return SQLType.VARCHAR;
        if (type.equals(char.class))
            return SQLType.CHAR;
        if (type.isEnum())
            return SQLType.ENUM;
        if (type.equals(boolean.class) || type.equals(Boolean.class) || type.equals(byte.class) || type.equals(Byte.class))
            return SQLType.TINYINT;
        if (type.equals(short.class) || type.equals(Short.class))
            return SQLType.SMALLINT;
        if (type.equals(int.class) || type.equals(Integer.class))
            return SQLType.INT;
        if (type.equals(double.class) || type.equals(Double.class))
            return SQLType.DOUBLE;
        if (type.equals(float.class) || type.equals(Float.class))
            return SQLType.FLOAT;
        if (type.equals(long.class) || type.equals(Long.class))
            return SQLType.BIGINT;
        if (type.equals(Timestamp.class))
            return SQLType.TIMESTAMP;
        if (type.equals(Date.class))
            return SQLType.DATE;
        if (type.equals(byte[].class))
            return SQLType.VARBINARY;
        return null;
    }

    public String getTypeParameters(Class<?> type, int size) {
        if (type.isEnum())
            return Arrays.stream(((Class<? extends Enum<?>>) type).getEnumConstants()).map(c -> "'" + c.name() + "'").collect(Collectors.joining(","));
        if (type.equals(String.class) || type.equals(char[].class))
            return size > 65535 || size < 1 ? null : String.valueOf(size);
        if (type.equals(byte[].class))
            return String.valueOf(size > 0 ? size : 255);
        if (type.equals(UUID.class))
            return "36";
        if (type.equals(boolean.class) || type.equals(Boolean.class) || type.equals(char.class))
            return "1";
        return null;
    }

}
