package org.javawebstack.orm.mapper;

public interface TypeMapper {

    Object mapToSQL(Object source, Class<?> type);
    Object mapToJava(Object source, Class<?> type);
    Class<?> getInternalType(Class<?> type);

}
