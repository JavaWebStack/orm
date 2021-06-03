package org.javawebstack.orm.mapper;

import org.javawebstack.orm.SQLType;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultMapper implements TypeMapper {

    // SOURCES:
    // TEXT Datatypes: https://www.mysqltutorial.org/mysql-text/
    private static final int BYTES_OVERHEAD_VARCHAR = 4;
    private static final int BYTES_OVERHEAD_TINYTEXT = 1;
    private static final int BYTES_OVERHEAD_TEXT = 2;
    private static final int BYTES_OVERHEAD_MEDIUMTEXT = 3;
    private static final int BYTES_OVERHEAD_LONGTEXT = 4;

    // The max sizes given in the manual are in bytes. There are overheads which need to be subtracted.
    // The following values assume utf8mb4 encoding which uses 4 bytes per character and
    // further quarters the maximum column length accordingly.
    private static final long MAX_SIZE_VARCHAR = (long) Math.floor((65535 - BYTES_OVERHEAD_VARCHAR) / 4);
    private static final long MAX_SIZE_MEDIUMTEXT = (long) Math.floor((16777215 - BYTES_OVERHEAD_MEDIUMTEXT) / 4);
    private static final long MAX_SIZE_LONGTEXT = (long) Math.floor((4294967295L - BYTES_OVERHEAD_LONGTEXT) / 4);

    public static final Map<String, Class<?>> TYPE_MAPPING = new HashMap<String, Class<?>>(){{
        put("FLOAT", Float.class);
        put("DOUBLE", Double.class);
        put("INT", Integer.class);
        put("BIGINT", Long.class);
        put("VARCHAR", String.class);
        put("TEXT", String.class);
        put("SHORTTEXT", String.class);
        put("LONGTEXT", String.class);
        put("TIMESTAMP", Timestamp.class);
        put("DATE", Date.class);
        put("VARBINARY", byte[].class);
        put("CHARARRAY", char[].class);
        put("SMALLINT", Short.class);
        put("TINYINT", Short.class);
    }};

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
        if (type.equals(char.class))
            return String.valueOf((char) source);

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
        if (type.equals(char.class)) {
            String stringSource = (String) source;
            if (stringSource.length() != 1)
                return ' ';
            else
                return stringSource.charAt(0);
        }
        return source;
    }

    public SQLType getType(Class<?> type, int size) {
        if (type.equals(String.class) || type.equals(char[].class))
            // Upper limit of 4294967295 exceeds the int boundaries
            if (size > MAX_SIZE_MEDIUMTEXT)
                return SQLType.LONGTEXT;
            if (size > MAX_SIZE_VARCHAR)
                return SQLType.MEDIUMTEXT;
            if (size > 1)
                return SQLType.VARCHAR;
            if (size == 1)
                return SQLType.CHAR;
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
            return size > MAX_SIZE_VARCHAR || size < 1 ? null : String.valueOf(size);
        if (type.equals(byte[].class))
            return String.valueOf(size > 0 ? size : 255);
        if (type.equals(UUID.class))
            return "36";
        if (type.equals(boolean.class) || type.equals(Boolean.class) || type.equals(char.class))
            return "1";
        return null;
    }

}
