package org.javawebstack.orm;

import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.annotation.SoftDelete;
import org.javawebstack.orm.annotation.Table;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.mapper.TypeMapper;
import org.javawebstack.orm.util.Helper;
import org.javawebstack.orm.util.KeyType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableInfo {

    private String idField = "id";
    private final String tableName;
    private final List<String> fieldNames = new ArrayList<>();
    private final Map<String, Field> fields = new HashMap<>();
    private final Map<String, String> fieldToColumn = new HashMap<>();
    private final Map<String, Column> fieldConfigs = new HashMap<>();
    private final Map<String, SQLType> sqlTypes = new HashMap<>();
    private final Map<String, String> sqlTypeParameters = new HashMap<>();
    private final ORMConfig config;
    private SoftDelete softDelete;
    private Dates dates;
    private final Class<? extends Model> modelClass;
    private String primaryKey;
    private final List<String> uniqueKeys = new ArrayList<>();
    private final Constructor<?> constructor;

    public TableInfo(Class<? extends Model> model, ORMConfig config) throws ORMConfigurationException {
        this.config = config;
        this.modelClass = model;
        if(model.isAnnotationPresent(Table.class)){
            Table table = model.getDeclaredAnnotationsByType(Table.class)[0];
            tableName = table.value();
        }else{
            tableName = Helper.toSnakeCase(model.getSimpleName())+"s";
        }
        try {
            constructor = model.getConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ORMConfigurationException("The model class has no empty constructor!");
        }
        for(Field field : model.getDeclaredFields()){
            if(Modifier.isStatic(field.getModifiers()))
                continue;
            if(!field.isAnnotationPresent(Column.class))
                continue;
            field.setAccessible(true);
            String fieldName = field.getName();
            fieldNames.add(fieldName);
            Column fieldConfig = field.getDeclaredAnnotationsByType(Column.class)[0];
            if(fieldConfig.name().length() > 0){
                fieldToColumn.put(fieldName, fieldConfig.name());
            }else{
                fieldToColumn.put(fieldName, config.isCamelToSnakeCase()?Helper.toSnakeCase(fieldName):fieldName);
            }
            fields.put(fieldName, field);
            fieldConfigs.put(fieldName, fieldConfig);
            SQLType sqlType = config.getType(field.getType(), fieldConfig.size());
            if(sqlType != null){
                sqlTypes.put(fieldName, sqlType);
                sqlTypeParameters.put(fieldName, config.getTypeParameters(field.getType(), fieldConfig.size()));
            }
            if(!sqlTypes.containsKey(fieldName))
                throw new ORMConfigurationException("Couldn't find type-mapper for '"+fieldName+"'!");
            if(fieldConfig.id()) {
                idField = fieldName;
            }
            if(fieldConfig.key() == KeyType.PRIMARY){
                if(primaryKey != null && !primaryKey.equals(fieldName))
                    throw new ORMConfigurationException("Multiple primary key fields!");
                primaryKey = fieldName;
            }
            if(fieldConfig.key() == KeyType.UNIQUE)
                uniqueKeys.add(fieldName);
        }
        if(!fields.containsKey(idField))
            idField = "uuid";
        if(!fields.containsKey(idField))
            throw new ORMConfigurationException("No id field found!");
        if(config.isIdPrimaryKey()){
            if(primaryKey == null)
                primaryKey = idField;
        }
        if(model.isAnnotationPresent(SoftDelete.class)){
            softDelete = model.getDeclaredAnnotationsByType(SoftDelete.class)[0];
            if(!fields.containsKey(softDelete.value()))
                throw new ORMConfigurationException("Missing soft-delete field '"+softDelete.value()+"'");
        }
        if(model.isAnnotationPresent(Dates.class)){
            dates = model.getDeclaredAnnotationsByType(Dates.class)[0];
            if(!fields.containsKey(dates.create()))
                throw new ORMConfigurationException("Missing dates field '"+dates.create()+"'");
            if(!fields.containsKey(dates.update()))
                throw new ORMConfigurationException("Missing dates field '"+dates.update()+"'");
        }
    }

    public boolean isSoftDelete(){
        return softDelete != null;
    }

    public SoftDelete getSoftDelete() {
        return softDelete;
    }

    public boolean hasDates(){
        return dates != null;
    }

    public boolean isAutoIncrement(){
        return fieldConfigs.get(idField).ai();
    }

    public String getSoftDeleteField(){
        return softDelete.value();
    }

    public String getCreatedField(){
        return dates.create();
    }

    public String getUpdatedField(){
        return dates.update();
    }

    public List<String> getFields(){
        return fieldNames;
    }

    public Field getField(String fieldName){
        return fields.get(fieldName);
    }

    public String getColumnName(String fieldName){
        String[] spl = fieldName.split("\\.");
        fieldName = spl[spl.length-1];
        if(fieldToColumn.containsKey(fieldName))
            return fieldToColumn.get(fieldName);
        return fieldName;
    }

    public SQLType getType(String fieldName){
        return sqlTypes.get(fieldName);
    }

    public String getTypeParameters(String fieldName){
        return sqlTypeParameters.get(fieldName);
    }

    public String getRawTableName(){
        return tableName;
    }

    public String getTablePrefix(){
        return config.getTablePrefix();
    }

    public String getTableName(){
        return config.getTablePrefix()+tableName;
    }

    public Class<? extends Model> getModelClass(){
        return modelClass;
    }

    public ORMConfig getConfig(){
        return config;
    }

    public String getPrimaryKey(){
        return primaryKey;
    }

    public List<String> getUniqueKeys(){
        return uniqueKeys;
    }

    public String getIdField(){
        return idField;
    }

    public Class<?> getIdType(){
        return getField(getIdField()).getType();
    }

    public Object getDefault(String fieldName){
        return null;
    }

    public boolean isNotNull(String fieldName){
        if(idField.equals(fieldName))
            return false;
        return false;
    }

    public Constructor<?> getModelConstructor(){
        return constructor;
    }

}
