package org.javawebstack.orm.mapper;

import org.javawebstack.orm.SQLType;

public interface TypeMapper {

    Object mapToSQL(Object source, Class<?> type);
    Object mapToJava(Object source, Class<?> type);
    SQLType getType(Class<?> type, int size);
    default String getTypeParameters(Class<?> type, int size){
        return null;
    }

}
