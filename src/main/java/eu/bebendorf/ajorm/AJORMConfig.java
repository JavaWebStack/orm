package eu.bebendorf.ajorm;

import eu.bebendorf.ajorm.mapper.DefaultMapper;
import eu.bebendorf.ajorm.mapper.TypeMapper;

import java.util.ArrayList;
import java.util.List;

public class AJORMConfig {
    private String tablePrefix = "";
    private boolean camelToSnakeCase = true;
    private int defaultSize = 0;
    private boolean idPrimaryKey = true;
    private List<TypeMapper> typeMappers = new ArrayList<>();
    public AJORMConfig(){
        typeMappers.add(new DefaultMapper());
    }
    public AJORMConfig setTablePrefix(String tablePrefix){
        this.tablePrefix = tablePrefix;
        return this;
    }
    public AJORMConfig setCamelToSnakeCase(boolean camelToSnakeCase){
        this.camelToSnakeCase = camelToSnakeCase;
        return this;
    }
    public AJORMConfig setDefaultSize(int defaultSize){
        this.defaultSize = defaultSize;
        return this;
    }
    public AJORMConfig addTypeMapper(TypeMapper typeMapper){
        typeMappers.add(typeMapper);
        return this;
    }
    public AJORMConfig setIdPrimaryKey(boolean idPrimaryKey){
        this.idPrimaryKey = idPrimaryKey;
        return this;
    }
    public boolean isCamelToSnakeCase() {
        return camelToSnakeCase;
    }
    public String getTablePrefix() {
        return tablePrefix;
    }
    public int getDefaultSize(){
        return defaultSize;
    }
    public List<TypeMapper> getTypeMappers(){
        return typeMappers;
    }
    public boolean isIdPrimaryKey() {
        return idPrimaryKey;
    }
}
