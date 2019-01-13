package eu.bebendorf.ajorm;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

public class Table<ObjectType,KeyType> {

    private SQL sql;
    private String prefix;
    private String tableName;
    private Map<String, Field> fieldReflection = new HashMap<>();
    private Map<Field, DatabaseField> fieldDescriptors = new HashMap<>();
    private String keyField;
    private Class<ObjectType> objectClass;

    public ObjectType queryById(KeyType id){
        QueryResult result = builder().where().eq(getColName(keyField),id).query();
        return result.first();
    }

    public List<ObjectType> queryForEq(String colName,Object value){
        QueryResult result = builder().where().eq(colName,value).query();
        return result.all();
    }

    public List<ObjectType> queryForAll(){
        QueryResult result = builder().query();
        return result.all();
    }

    public ObjectType query(ObjectType object){
        return builder().where().eq(getColName(keyField),getKeyValue(object)).query(object);
    }

    public QueryBuilder builder(){
        return new QueryBuilder(this);
    }

    public void create(ObjectType object){
        builder().insert(object);
    }

    public void delete(ObjectType object){
        deleteById(getKeyValue(object));
    }

    public void deleteById(KeyType id){
        builder().where().eq(getColName(keyField),id).delete();
    }

    public void update(ObjectType object){
        builder().where().eq(getColName(keyField),getKeyValue(object)).update(object);
    }

    public ObjectType reload(ObjectType object){
        return queryById(getKeyValue(object));
    }

    private String getTableName(){
        return prefix+tableName;
    }

    public String getKeyColName(){
        return getColName(keyField);
    }

    private String getColName(String fieldName){
        Field field = fieldReflection.get(fieldName);
        DatabaseField descriptor = fieldDescriptors.get(field);
        String colName = descriptor.columnName();
        if(colName.length()==0)
            colName = fieldName;
        return colName;
    }

    private KeyType getKeyValue(ObjectType object){
        try {
            return (KeyType)fieldReflection.get(keyField).get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convert(Class type,Object value){
        if(type.isEnum())
            return ((Enum<?>)value).name();
        if(type.equals(UnixTime.class))
            return String.valueOf(((UnixTime)value).millis());
        if(type.equals(boolean.class))
            return ((boolean)value)?"1":"0";
        return String.valueOf(value);
    }

    public Table(SQL sql, Class<ObjectType> clazz){
        this(sql,"",clazz);
    }

    public Table(SQL sql, String prefix,Class<ObjectType> clazz){
        this.sql = sql;
        this.prefix = prefix;
        this.objectClass = clazz;
        DatabaseTable tableInfo = objectClass.getDeclaredAnnotationsByType(DatabaseTable.class)[0];
        this.tableName = tableInfo.value();
        for(Field field : objectClass.getDeclaredFields()){
            DatabaseField[] annotations = field.getDeclaredAnnotationsByType(DatabaseField.class);
            if(annotations.length>0){
                field.setAccessible(true);
                DatabaseField colInfo = annotations[0];
                fieldReflection.put(field.getName(),field);
                fieldDescriptors.put(field,colInfo);
                if(colInfo.id())
                    keyField = field.getName();
            }
        }
    }

    public class QueryBuilder {

        private Table table;

        private StringBuilder querySelector = new StringBuilder();
        private List<Object> parameters = new ArrayList<>();

        public QueryBuilder(Table table){
            this.table = table;
        }

        public QueryBuilder where(){
            querySelector.append(" WHERE");
            return this;
        }

        public QueryBuilder eq(String columnName,Object value){
            querySelector.append(" `"+columnName+"`=?");
            parameters.add(value);
            return this;
        }

        public QueryBuilder like(String columnName,Object search){
            querySelector.append(" `"+columnName+"` LIKE ?");
            parameters.add(search);
            return this;
        }

        public QueryBuilder and(){
            querySelector.append(" AND");
            return this;
        }

        public QueryBuilder or(){
            querySelector.append(" OR");
            return this;
        }

        public QueryBuilder isNull(String columnName){
            querySelector.append(" `"+columnName+"` IS NULL");
            return this;
        }

        public QueryBuilder notNull(String columnName){
            querySelector.append(" `"+columnName+"` IS NOT NULL");
            return this;
        }

        public QueryBuilder limit(int amount){
            querySelector.append(" LIMIT "+amount);
            return this;
        }

        public void delete(){
            sql.write("DELETE FROM `"+getTableName()+"`"+querySelector.toString()+";",parameters.toArray());
        }

        public QueryResult query(){
            ResultSet rs = sql.read("SELECT * FROM `"+getTableName()+"`"+querySelector.toString()+";",parameters.toArray());
            List<ObjectType> objects = parseResult(rs);
            sql.close(rs);
            return new QueryResult(objects);
        }

        public ObjectType query(ObjectType object){
            ResultSet rs = sql.read("SELECT * FROM `"+getTableName()+"`"+querySelector.toString()+";",parameters.toArray());
            try {
                if(rs.next())
                    parseResult(rs, object);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sql.close(rs);
            return object;
        }

        public int count(){
            ResultSet rs = sql.read("SELECT COUNT(*) FROM `"+getTableName()+"`"+querySelector.toString()+";",parameters.toArray());
            try {
                if(rs.next())
                    return rs.getInt(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sql.close(rs);
            return -1;
        }

        public void update(ObjectType object){
            Map<String,String> data = parseObject(object);
            String idColName = getColName(keyField);
            String updateData = "";
            List<Object> updateParameters = new ArrayList<>();
            for(String colName : data.keySet())
                if(!idColName.equals(colName)){
                    updateData+=",`"+colName+"`=?";
                    updateParameters.add(data.get(colName));
                }
            if(updateData.length()>0)
                updateData = updateData.substring(1);
            for(Object param : parameters)
                updateParameters.add(param);
            sql.write("UPDATE `"+getTableName()+"` SET "+updateData+querySelector.toString()+";",updateParameters.toArray());
        }

        public void insert(ObjectType object){
            Map<String,String> data = parseObject(object);
            String idColName = getColName(keyField);
            String insertData = "";
            String insertKeys = "";
            for(String colName : data.keySet()){
                if(idColName.equals(colName) && fieldDescriptors.get(fieldReflection.get(keyField)).ai())
                    continue;
                insertKeys+=",`"+colName+"`";
                insertData+=",?";
                parameters.add(data.get(colName));

            }
            if(insertData.length()>0)
                insertData = insertData.substring(1);
            if(insertKeys.length()>0)
                insertKeys = insertKeys.substring(1);
            sql.write("INSERT INTO `"+getTableName()+"` ("+insertKeys+") VALUES ("+insertData+");",parameters.toArray());
        }

        private Map<String,String> parseObject(ObjectType object){
            Map<String,String> data = new HashMap<>();
            for(String fieldName : fieldReflection.keySet()){
                Field field = fieldReflection.get(fieldName);
                try {
                    String value = convert(field.getType(),field.get(object));
                    String colName = getColName(fieldName);
                    data.put(colName,value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return data;
        }

        private List<ObjectType> parseResult(ResultSet resultSet){
            List<ObjectType> objects = new ArrayList<>();
            try {
                while(resultSet.next()){
                    objects.add(parseResult(resultSet, objectClass.newInstance()));
                }
            }catch(Exception ex){ex.printStackTrace();}
            return objects;
        }

        private ObjectType parseResult(ResultSet resultSet, ObjectType object){
            try {
                for(String fieldName : fieldReflection.keySet()){
                    Field field = fieldReflection.get(fieldName);
                    DatabaseField descriptor = fieldDescriptors.get(field);
                    String colName = descriptor.columnName();
                    if(colName.length()==0)
                        colName = fieldName;
                    field.set(object, getValue(resultSet, colName, field.getType()));
                }
            }catch(Exception ex){ex.printStackTrace();}
            return object;
        }

        private Object getValue(ResultSet resultSet, String colName, Class type) throws SQLException {
            Object value = null;
            if(type.isEnum())
                value = Enum.valueOf((Class<Enum>) type, resultSet.getString(colName));
            if(type.equals(int.class))
                value = resultSet.getInt(colName);
            if(type.equals(short.class))
                value = resultSet.getShort(colName);
            if(type.equals(double.class))
                value = resultSet.getDouble(colName);
            if(type.equals(long.class))
                value = resultSet.getLong(colName);
            if(type.equals(boolean.class))
                value = resultSet.getBoolean(colName);
            if(type.equals(float.class))
                value = resultSet.getFloat(colName);
            if(type.equals(Timestamp.class))
                value = resultSet.getTimestamp(colName);
            if(type.equals(Date.class))
                value = resultSet.getDate(colName);
            if(type.equals(Time.class))
                value = resultSet.getTime(colName);
            if(type.equals(UnixTime.class))
                value = new UnixTime(resultSet.getLong(colName));
            if(value==null)
                value = resultSet.getString(colName);
            return value;
        }

    }

    public class QueryResult {
        private List<ObjectType> all;
        public QueryResult(List<ObjectType> all){
            this.all = all;
        }
        public List<ObjectType> all(){
            return this.all;
        }
        public ObjectType first(){
            return all.size()>0?get(0):null;
        }
        public ObjectType get(int index){
            return all.get(index);
        }
        public int count(){
            return all.size();
        }
    }

}
