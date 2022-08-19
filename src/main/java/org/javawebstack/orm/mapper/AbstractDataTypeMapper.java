package org.javawebstack.orm.mapper;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.orm.SQLType;

public class AbstractDataTypeMapper implements TypeMapper {

    private static final int BYTES_OVERHEAD_TEXT = 2;
    private static final int BYTES_OVERHEAD_MEDIUMTEXT = 3;
    private static final int BYTES_OVERHEAD_LONGTEXT = 4;

    private static final long MAX_SIZE_TEXT = (long) Math.floor((65535 - BYTES_OVERHEAD_TEXT) / 4);
    private static final long MAX_SIZE_MEDIUMTEXT = (long) Math.floor((16777215 - BYTES_OVERHEAD_MEDIUMTEXT) / 4);
    private static final long MAX_SIZE_LONGTEXT = (long) Math.floor((4294967295L - BYTES_OVERHEAD_LONGTEXT) / 4);

    public Object mapToSQL(Object source, Class<?> type) {
        if (source == null)
            return null;
        if (source instanceof AbstractElement)
            return ((AbstractElement) source).toJsonString();
        return source;
    }

    public Object mapToJava(Object source, Class<?> type) {
        if (source == null)
            return null;
        if (AbstractElement.class.isAssignableFrom(type))
            return AbstractElement.fromJson((String) source);
        return source;
    }

    public SQLType getType(Class<?> type, int size) {
        if (AbstractElement.class.isAssignableFrom(type)) {
            if (size > MAX_SIZE_MEDIUMTEXT)
                return SQLType.LONGTEXT;
            if (size > MAX_SIZE_TEXT)
                return SQLType.MEDIUMTEXT;
            return SQLType.TEXT;
        }
        return null;
    }

    public String getTypeParameters(Class<?> type, int size) {
        return null;
    }
}
