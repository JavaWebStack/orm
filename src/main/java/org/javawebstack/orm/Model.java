package org.javawebstack.orm;

import org.javawebstack.injector.Injector;
import org.javawebstack.orm.query.Query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Model {

    private static final Method saveMethod;
    private static final Method deleteMethod;
    private static final Method finalDeleteMethod;
    private static final Method restoreMethod;
    private static final Method refreshMethod;

    {
        inject();
        updateOriginal();
    }

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

    private transient boolean internalEntryExists = false;
    private transient final Map<Class<? extends Model>, Object> internalJoinedModels = new HashMap<>();
    private transient Map<String, Object> internalOriginalValues = new HashMap<>();
    private transient Map<String, Object> internalExtraFields = new HashMap<>();

    void internalAddJoinedModel(Class<? extends Model> type, Object entity) {
        internalJoinedModels.put(type, entity);
    }

    void updateOriginal() {
        internalOriginalValues = getFieldValues();
    }

    public Map<String, Object> getFieldValues() {
        TableInfo info = Repo.get(getClass()).getInfo();
        Map<String, Object> values = new HashMap<>();
        for (String field : info.getFields()) {
            try {
                values.put(field, info.getField(field).get(this));
            } catch (IllegalAccessException ignored) {
            }
        }
        return values;
    }

    public Map<String, Object> getExtraFields() {
        return internalExtraFields;
    }

    public <T> T getExtraField(String key) {
        return (T) internalExtraFields.get(key);
    }

    public Map<String, Object> getOriginalValues() {
        return internalOriginalValues;
    }

    public <T> T getOriginalValue(String field) {
        if (internalOriginalValues.get(field) == null)
            return null;
        return (T) internalOriginalValues.get(field);
    }

    public boolean isDirty(String... fields) {
        List<String> dirty = getDirtyFields();
        if(fields.length == 0 && dirty.size() > 0)
            return true;
        for (String f : fields) {
            if (dirty.contains(f))
                return true;
        }
        return false;
    }

    public List<String> getDirtyFields() {
        List<String> dirty = new ArrayList<>();
        Map<String, Object> original = getOriginalValues();
        Map<String, Object> current = getFieldValues();
        for (String key : current.keySet()) {
            Object o = original.get(key);
            Object c = current.get(key);
            if (o == null && c == null)
                continue;
            if (o == null || !o.equals(c))
                dirty.add(key);
        }
        return dirty;
    }

    public <T extends Model> T getJoined(Class<T> model) {
        return (T) internalJoinedModels.get(model);
    }

    public boolean hasJoined(Class<? extends Model> model) {
        return internalJoinedModels.containsKey(model);
    }

    boolean doesEntryExist() {
        return internalEntryExists;
    }

    void setEntryExists(boolean exists) {
        this.internalEntryExists = exists;
    }

    public void inject() {
        Injector injector = Repo.get(getClass()).getInfo().getConfig().getInjector();
        if (injector != null)
            injector.inject(this);
    }

    public void save() {
        try {
            saveMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        try {
            deleteMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void finalDelete() {
        try {
            finalDeleteMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void restore() {
        try {
            restoreMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh() {
        try {
            refreshMethod.invoke(ORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> Query<T> belongsTo(Class<T> parent) {
        return belongsTo(parent, Repo.get(parent).getInfo().getRelationField());
    }

    public <T extends Model> Query<T> belongsTo(Class<T> parent, String fieldName) {
        return belongsTo(parent, fieldName, Repo.get(parent).getInfo().getIdField());
    }

    public <T extends Model> Query<T> belongsTo(Class<T> parent, String fieldName, String otherFieldName) {
        try {
            Object id = Repo.get(getClass()).getInfo().getField(fieldName).get(this);
            return Repo.get(parent).where(otherFieldName, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> void assignTo(T value) {
        if(value == null)
            throw new InvalidParameterException("You need to specify a parent type if the value is null");
        assignTo((Class<T>) value.getClass(), value);
    }

    public <T extends Model> void assignTo(Class<T> parent, T value) {
        assignTo(parent, value, Repo.get(parent).getInfo().getRelationField());
    }

    public <T extends Model> void assignTo(Class<T> parent, T value, String fieldName) {
        assignTo(parent, value, fieldName, Repo.get(parent).getInfo().getIdField());
    }

    public <T extends Model> void assignTo(Class<T> parent, T value, String fieldName, String otherFieldName) {
        try {
            Field f = Repo.get(getClass()).getInfo().getField(fieldName);
            if (value == null) {
                f.set(this, null);
            } else {
                Repo<T> repo = Repo.get(parent);
                Object id = repo.getInfo().getField(otherFieldName).get(value);
                f.set(this, id);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model> Query<T> hasMany(Class<T> child) {
        return hasMany(child, Repo.get(getClass()).getInfo().getRelationField());
    }

    public <T extends Model> Query<T> hasMany(Class<T> child, String fieldName) {
        return hasMany(child, fieldName, Repo.get(getClass()).getInfo().getIdField());
    }

    public <T extends Model> Query<T> hasMany(Class<T> child, String fieldName, String ownFieldName) {
        try {
            Repo<?> ownRepo = Repo.get(getClass());
            Object id = ownRepo.getInfo().getField(ownFieldName).get(this);
            return Repo.get(child).where(fieldName, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot) {
        return belongsToMany(other, pivot, null);
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot, Function<Query<P>, Query<P>> pivotFilter) {
        return belongsToMany(other, pivot, Repo.get(getClass()).getInfo().getRelationField(), Repo.get(other).getInfo().getRelationField(), pivotFilter);
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot, String selfPivotFieldName, String otherPivotFieldName) {
        return belongsToMany(other, pivot, selfPivotFieldName, otherPivotFieldName, null);
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot, String selfPivotFieldName, String otherPivotFieldName, Function<Query<P>, Query<P>> pivotFilter) {
        return belongsToMany(other, pivot, selfPivotFieldName, otherPivotFieldName, Repo.get(getClass()).getInfo().getIdField(), Repo.get(other).getInfo().getIdField(), pivotFilter);
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot, String selfPivotFieldName, String otherPivotFieldName, String selfFieldName, String otherFieldName) {
        return belongsToMany(other, pivot, selfPivotFieldName, otherPivotFieldName, selfFieldName, otherFieldName, null);
    }

    public <T extends Model, P extends Model> Query<T> belongsToMany(Class<T> other, Class<P> pivot, String selfPivotFieldName, String otherPivotFieldName, String selfFieldName, String otherFieldName, Function<Query<P>, Query<P>> pivotFilter) {
        try {
            Repo<?> selfRepo = Repo.get(getClass());
            Repo<T> otherRepo = Repo.get(other);
            Object id = selfRepo.getInfo().getField(selfFieldName).get(this);
            return otherRepo.whereExists(pivot, q -> {
                q.where(pivot, selfPivotFieldName, "=", id).where(pivot, otherPivotFieldName, "=", other, otherFieldName);
                if (pivotFilter != null)
                    q = pivotFilter.apply(q);
                return q;
            });
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
