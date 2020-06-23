package eu.bebendorf.ajorm.mapper;

public interface TypeMapper {

    Object mapToSQL(Object source, Class<?> type);
    Object mapToJava(Object source, Class<?> type);
    Class<?> getTargetType(Class<?> type);
    String getSQLType(Class<?> type, int size);

}
