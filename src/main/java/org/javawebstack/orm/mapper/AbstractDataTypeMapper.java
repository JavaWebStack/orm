package org.javawebstack.orm.mapper;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.orm.SQLType;

public class AbstractDataTypeMapper implements TypeMapper {
    public Object mapToSQL(Object source, Class<?> type) {
        if(source == null)
            return null;
        if(source instanceof AbstractElement)
            return ((AbstractElement) source).toJsonString();
        return source;
    }

    public Object mapToJava(Object source, Class<?> type) {
        if(source == null)
            return null;
        if(AbstractElement.class.isAssignableFrom(type))
            return AbstractElement.fromJson((String) source);
        return source;
    }

    public SQLType getType(Class<?> type, int size){
        if(AbstractElement.class.isAssignableFrom(type))
            return SQLType.TEXT;
        return null;
    }

    public String getTypeParameters(Class<?> type, int size){
        return null;
    }
}
