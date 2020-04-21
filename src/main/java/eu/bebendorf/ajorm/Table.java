package eu.bebendorf.ajorm;

import eu.bebendorf.ajorm.event.EventBus;

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
    private List<String> fieldNames;
    private Map<String, Field> fieldReflection = new HashMap<>();
    private Map<Field, DatabaseField> fieldDescriptors = new HashMap<>();
    private String keyField;
    private Class<ObjectType> objectClass;
    private EventBus<ObjectType> eventBus = new EventBus<>();

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
        eventBus.beforeCreate(object);
        builder().insert(object);
        eventBus.afterCreate(object);
    }

    public void delete(ObjectType object){
        eventBus.beforeDelete(object);
        deleteById(getKeyValue(object));
        eventBus.afterDelete(object);
    }

    public void deleteById(KeyType id){
        builder().where().eq(getColName(keyField),id).delete();
    }

    public void update(ObjectType object){
        eventBus.beforeUpdate(object);
        builder().where().eq(getColName(keyField),getKeyValue(object)).update(object);
        eventBus.afterUpdate(object);
    }

    public ObjectType reload(ObjectType object){
        return queryById(getKeyValue(object));
    }

    public String getTableName(){
        return prefix+tableName;
    }

    public TableInfo getInfo(){
        return new TableInfo(this, fieldNames, fieldReflection, fieldDescriptors);
    }

    public void migrate(){
        MigrationTool.migrate(this);
    }

    public String getKeyColName(){
        return getColName(keyField);
    }

    public String getColName(String fieldName){
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
        if(value==null)
            return null;
        if(type.isEnum())
            return ((Enum<?>)value).name();
        if(type.equals(UnixTime.class))
            return String.valueOf(((UnixTime)value).millis());
        if(type.equals(boolean.class))
            return ((boolean)value)?"1":"0";
        if(type.equals(UUID.class))
            return value.toString();
        return String.valueOf(value);
    }

    public SQL getConnection(){
        return sql;
    }

    public Table(SQL sql, Class<ObjectType> clazz){
        this(sql,"",clazz);
    }

    public Table(SQL sql, String prefix,Class<ObjectType> clazz){
        this.sql = sql;
        this.prefix = prefix;
        this.objectClass = clazz;
        DatabaseTable tableInfo = objectClass.getDeclaredAnnotation(DatabaseTable.class);
        this.tableName = tableInfo.value();
        fieldNames = new ArrayList<>();
        for(Field field : objectClass.getDeclaredFields()){
            DatabaseField[] annotations = field.getDeclaredAnnotationsByType(DatabaseField.class);
            if(annotations.length>0){
                field.setAccessible(true);
                fieldNames.add(field.getName());
                DatabaseField colInfo = annotations[0];
                fieldReflection.put(field.getName(),field);
                fieldDescriptors.put(field,colInfo);
                if(colInfo.id())
                    keyField = field.getName();
            }
        }
    }

    public Table<ObjectType, KeyType> events(){
        eventBus.check(objectClass);
        return this;
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

        public QueryBuilder c(String custom, Object... params){
            querySelector.append(custom);
            for(Object param : params){
                parameters.add(sqlSafe(param));
            }
            return this;
        }

        public QueryBuilder eq(String columnName,Object value){
            querySelector.append(" `"+columnName+"`=?");
            parameters.add(sqlSafe(value));
            return this;
        }

        public QueryBuilder like(String columnName,Object search){
            querySelector.append(" `"+columnName+"` LIKE ?");
            parameters.add(sqlSafe(search));
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

        public QueryBuilder order(String column, boolean desc){
            querySelector.append(" ORDER BY `"+column+"`"+(desc?" DESC":""));
            return this;
        }

        private Object sqlSafe(Object object){
            if(object.getClass().equals(UUID.class)){
                return object.toString();
            }
            return object;
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
                    return rs.getInt(1);
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
                if(idColName.equals(colName) && data.get(idColName).equals("0") && fieldDescriptors.get(fieldReflection.get(keyField)).ai())
                    continue;
                insertKeys+=",`"+colName+"`";
                insertData+=",?";
                parameters.add(data.get(colName));

            }
            if(insertData.length()>0)
                insertData = insertData.substring(1);
            if(insertKeys.length()>0)
                insertKeys = insertKeys.substring(1);
            int id = sql.write("INSERT INTO `"+getTableName()+"` ("+insertKeys+") VALUES ("+insertData+");",parameters.toArray());
            if(data.get(idColName).equals("0")){
                try {
                    fieldReflection.get(keyField).set(object, id);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
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
            if(resultSet.getString(colName) == null)
                return null;
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
            if(type.equals(UUID.class))
                value = UUID.fromString(resultSet.getString(colName));
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
