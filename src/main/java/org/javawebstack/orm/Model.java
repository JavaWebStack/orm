package org.javawebstack.orm;

import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.util.Helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Model {

    private static final Method saveMethod;
    private static final Method deleteMethod;
    private static final Method finalDeleteMethod;
    private static final Method restoreMethod;
    private static final Method refreshMethod;

    static {
        try {
            saveMethod = Repo.class.getMethod("save", Model.class);
            deleteMethod = Repo.class.getMethod("delete", Model.class);
            finalDeleteMethod = Repo.class.getMethod("finalDelete", Model.class);
            restoreMethod = Repo.class.getMethod("restore", Model.class);
            refreshMethod = Repo.class.getMethod("refresh", Model.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean internalEntryExists = false;
    private final Map<Class<? extends Model>, Object> internalJoinedModels = new HashMap<>();

    void internalAddJoinedModel(Class<? extends Model> type, Object entity){
        internalJoinedModels.put(type, entity);
    }

    public <T extends Model> T getJoined(Class<T> model){
        return (T) internalJoinedModels.get(model);
    }

    public boolean hasJoined(Class<? extends Model> model){
        return internalJoinedModels.containsKey(model);
    }

    boolean doesEntryExist(){
        return internalEntryExists;
    }

    void setEntryExists(boolean exists){
        this.internalEntryExists = exists;
    }

    public void save(){
        try {
            saveMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void delete(){
        try {
            deleteMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void finalDelete(){
        try {
            finalDeleteMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void restore(){
        try {
            restoreMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh(){
        try {
            refreshMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> T belongsTo(Class<T> parent){
        return belongsTo(parent, Helper.pascalToCamelCase(parent.getSimpleName())+"Id");
    }

    public <T extends Model> T belongsTo(Class<T> parent, String fieldName){
        try {
            Integer id = (Integer) Repo.get(getClass()).getInfo().getField(fieldName).get(this);
            if(id == null)
                return null;
            return Repo.get(parent).get(id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> void assignTo(Class<T> parent, T value){
        assignTo(parent, value, Helper.pascalToCamelCase(parent.getSimpleName())+"Id");
    }

    public <T extends Model> void assignTo(Class<T> parent, T value, String fieldName){
        try {
            Field f = Repo.get(getClass()).getInfo().getField(fieldName);
            if(value == null){
                f.set(this, null);
            }else{
                Repo<T> repo = Repo.get(parent);
                Integer id = (Integer) repo.getInfo().getField(repo.getInfo().getIdField()).get(value);
                f.set(this, id);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> List<T> hasMany(Class<T> child){
        return hasManyRelation(child).all();
    }

    public <T extends Model> List<T> hasMany(Class<T> child, String fieldName){
        return hasManyRelation(child, fieldName).all();
    }

    public <T extends Model> Query<T> hasManyRelation(Class<T> child){
        return hasManyRelation(child, Helper.pascalToCamelCase(getClass().getSimpleName())+"Id");
    }

    public <T extends Model> Query<T> hasManyRelation(Class<T> child, String fieldName){
        try {
            Repo<?> ownRepo = Repo.get(getClass());
            Integer id = (Integer) ownRepo.getInfo().getField(ownRepo.getInfo().getIdField()).get(this);
            return Repo.get(child).where(fieldName, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> List<T> belongsToMany(Class<T> other, Class<? extends Model> pivot){
        return belongsToMany(other, pivot, Helper.pascalToCamelCase(getClass().getSimpleName())+"Id", Helper.pascalToCamelCase(other.getSimpleName())+"Id");
    }

    public <T extends Model> List<T> belongsToMany(Class<T> other, Class<? extends Model> pivot, String selfFieldName, String otherFieldName){
        try {
            Repo<?> selfRepo = Repo.get(getClass());
            Repo<T> otherRepo = Repo.get(other);
            Field otherField = Repo.get(pivot).getInfo().getField(otherFieldName);
            Integer id = (Integer) selfRepo.getInfo().getField(selfRepo.getInfo().getIdField()).get(this);
            return Repo.get(pivot).where(selfFieldName, id).stream().map(p -> {
                try {
                    Integer otherId = (Integer) otherField.get(p);
                    return otherRepo.get(otherId);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
