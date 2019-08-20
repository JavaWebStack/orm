package eu.bebendorf.ajorm;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TableInfo {

    private Table table;
    private List<String> fieldNames;
    private Map<String, Field> fields;
    private Map<Field, DatabaseField> infos;

    public TableInfo(Table table, List<String> fieldNames, Map<String, Field> fields, Map<Field, DatabaseField> infos){
        this.table = table;
        this.fieldNames = fieldNames;
        this.fields = fields;
        this.infos = infos;
    }

    public List<String> getFieldNames(){
        return fieldNames;
    }

    public String getColName(String name){
        return table.getColName(name);
    }

    public Class<?> getJavaType(String name){
        if(!fields.containsKey(name))
            return null;
        return fields.get(name).getType();
    }

    private static String getSQLType(Class<?> type){
        if(type.equals(String.class))
            return "TEXT";
        if(type.equals(UUID.class))
            return "VARCHAR";
        if(type.equals(boolean.class))
            return "INT";
        if(type.equals(int.class))
            return "INT";
        if(type.equals(double.class))
            return "DOUBLE";
        if(type.equals(float.class))
            return "FLOAT";
        if(type.equals(long.class))
            return "BIGINT";
        if(type.equals(Timestamp.class))
            return "TIMESTAMP";
        if(type.equals(UnixTime.class))
            return "BIGINT";
        return null;
    }

    private static String getSQLSize(Class<?> type){
        if(type.equals(UUID.class))
            return "36";
        if(type.equals(boolean.class))
            return "1";
        if(type.equals(int.class))
            return "11";
        if(type.equals(long.class))
            return "20";
        if(type.equals(UnixTime.class))
            return "20";
        return null;
    }

    public String getSQLType(String name){
        Class<?> type = getJavaType(name);
        if(type == null)
            return null;
        return getSQLType(type);
    }

    public String getSQLSize(String name){
        Class<?> type = getJavaType(name);
        if(type == null)
            return null;
        String size = getSQLSize(type);
        if(size == null){
            int l = getAnnotation(name).length();
            size = l != -1 ? String.valueOf(l) : null;
        }
        return size;
    }

    public String getSQLSizedType(String name){
        String type = getSQLType(name);
        if(type == null)
            return null;
        String size = getSQLSize(name);
        if(size != null){
            if(type.equals("TEXT")){
                type = "VARCHAR";
            }
            return type+"("+size+")";
        }
        return type;
    }

    private DatabaseField getAnnotation(String name){
        if(!fields.containsKey(name))
            return null;
        Field f = fields.get(name);
        if(!infos.containsKey(f))
            return null;
        return infos.get(f);
    }

    public boolean isAutoIncrement(String name){
        DatabaseField info = getAnnotation(name);
        if(info == null)
            return false;
        return info.ai();
    }

    public boolean isPrimaryKey(String name){
        DatabaseField info = getAnnotation(name);
        if(info == null)
            return false;
        return info.primary();
    }

    public boolean isUniqueKey(String name){
        DatabaseField info = getAnnotation(name);
        if(info == null)
            return false;
        return info.unique();
    }

    public boolean isId(String name){
        DatabaseField info = getAnnotation(name);
        if(info == null)
            return false;
        return info.id();
    }



}
