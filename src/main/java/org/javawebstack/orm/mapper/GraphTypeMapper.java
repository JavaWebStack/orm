package org.javawebstack.orm.mapper;

import org.javawebstack.graph.GraphElement;
import org.javawebstack.orm.SQLType;

public class GraphTypeMapper implements TypeMapper {
    public Object mapToSQL(Object source, Class<?> type) {
        if(source == null)
            return null;
        if(source instanceof GraphElement)
            return ((GraphElement) source).toJsonString();
        return source;
    }

    public Object mapToJava(Object source, Class<?> type) {
        if(source == null)
            return null;
        if(GraphElement.class.isAssignableFrom(type))
            return GraphElement.fromJson((String) source);
        return source;
    }

    public SQLType getType(Class<?> type, int size){
        if(GraphElement.class.isAssignableFrom(type))
            return SQLType.TEXT;
        return null;
    }

    public String getTypeParameters(Class<?> type, int size){
        return null;
    }
}
