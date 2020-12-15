package org.javawebstack.orm;

import com.google.gson.annotations.Expose;
import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.util.Helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    @Expose(serialize = false, deserialize = false)
    private boolean internalEntryExists = false;
    @Expose(serialize = false, deserialize = false)
    private final Map<Class<? extends Model>, Object> internalJoinedModels = new HashMap<>();
    @Expose(serialize = false, deserialize = false)
    private final Map<String, Object> internalLastValue = new HashMap<>();

    void internalAddJoinedModel(Class<? extends Model> type, Object entity){
        internalJoinedModels.put(type, entity);
    }
    void internalSetLastValue(String key, Object value){
        if(value == null) internalLastValue.remove(key);
        else internalLastValue.put(key, value);
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

    public boolean isDirty(String... fields){
        Repo<?> repo = Repo.get(getClass());
        for(String field : fields){
            try {
                Object value = repo.getInfo().getField(field).get(this);
                Object oldValue = internalLastValue.get(field);
                if((value == null && oldValue != null) || (oldValue == null && value != null))
                    return true;
                if(value == null)
                    continue;
                if(!value.equals(oldValue))
                    return true;
            } catch (IllegalAccessException e) {
                throw new ORMQueryException(e);
            }
        }
        return false;
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

    public <T extends Model> Query<T> belongsTo(Class<T> parent){
        return belongsTo(parent, Repo.get(parent).getInfo().getRelationField());
    }

    public <T extends Model> Query<T> belongsTo(Class<T> parent, String fieldName){
        try {
            Object id = Repo.get(getClass()).getInfo().getField(fieldName).get(this);
            if(id == null)
                return null;
            return Repo.get(parent).whereId(id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> void assignTo(Class<T> parent, T value){
        assignTo(parent, value, Repo.get(parent).getInfo().getRelationField());
    }

    public <T extends Model> void assignTo(Class<T> parent, T value, String fieldName){
        try {
            Field f = Repo.get(getClass()).getInfo().getField(fieldName);
            if(value == null){
                f.set(this, null);
            }else{
                Repo<T> repo = Repo.get(parent);
                Object id = repo.getInfo().getField(repo.getInfo().getIdField()).get(value);
                f.set(this, id);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> Query<T> hasMany(Class<T> child){
        return hasMany(child, Repo.get(getClass()).getInfo().getRelationField());
    }

    public <T extends Model> Query<T> hasMany(Class<T> child, String fieldName){
        try {
            Repo<?> ownRepo = Repo.get(getClass());
            Object id = ownRepo.getInfo().getField(ownRepo.getInfo().getIdField()).get(this);
            return Repo.get(child).where(fieldName, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot){
        return belongsToMany(other, pivot, null);
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot, Function<Query<P>,Query<P>> pivotFilter){
        return belongsToMany(other, pivot, Repo.get(getClass()).getInfo().getRelationField(), Repo.get(other).getInfo().getRelationField(), pivotFilter);
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot, String selfFieldName, String otherFieldName){
        return belongsToMany(other, pivot, selfFieldName, otherFieldName, null);
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot, String selfFieldName, String otherFieldName, Function<Query<P>,Query<P>> pivotFilter){
        try {
            Repo<?> selfRepo = Repo.get(getClass());
            Repo<T> otherRepo = Repo.get(other);
            Object id = selfRepo.getInfo().getField(selfRepo.getInfo().getIdField()).get(this);
            return otherRepo.whereExists(pivot, q -> {
                q.where(pivot, selfFieldName, "=", id).where(pivot, otherFieldName, "=", other, otherRepo.getInfo().getIdColumn());
                if(pivotFilter != null)
                    q = pivotFilter.apply(q);
                return q;
            });
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMorph(String name, Class<? extends Model> type, Object id){
        TableInfo info = Repo.get(getClass()).getInfo();
        try {
            info.getField(name+"Id").set(this, id);
            info.getField(name+"Type").set(this, Repo.get(type).getInfo().getMorphType());
        } catch (IllegalAccessException ignored) {}
    }

    public void setMorph(String name, Model model){
        setMorph(name, model.getClass(), Repo.get(model.getClass()).getId(model));
    }

}
