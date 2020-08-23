package eu.bebendorf.ajorm;

import eu.bebendorf.ajorm.util.Helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Model {

    private static Method saveMethod;
    private static Method deleteMethod;
    private static Method finalDeleteMethod;
    private static Method restoreMethod;
    private static Method refreshMethod;

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

    public void save(){
        try {
            saveMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(){
        try {
            deleteMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void finalDelete(){
        try {
            finalDeleteMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void restore(){
        try {
            restoreMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh(){
        try {
            refreshMethod.invoke(AJORM.repo(getClass()), this);
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

    public <T extends Model> QueryBuilder<T> hasManyRelation(Class<T> child){
        return hasManyRelation(child, Helper.pascalToCamelCase(getClass().getSimpleName())+"Id");
    }

    public <T extends Model> QueryBuilder<T> hasManyRelation(Class<T> child, String fieldName){
        try {
            Repo<?> ownRepo = Repo.get(getClass());
            Integer id = (Integer) ownRepo.getInfo().getField(ownRepo.getInfo().getIdField()).get(this);
            return Repo.get(child).where(fieldName, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
