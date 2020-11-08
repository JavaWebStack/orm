package org.javawebstack.orm;

import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.migration.AutoMigrator;
import org.javawebstack.orm.wrapper.SQL;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Repo<T extends Model> {

    public static <T extends Model> Repo<T> get(Class<T> model){
        return ORM.repo(model);
    }

    private final TableInfo info;
    private final SQL connection;
    private final List<Observer<T>> observers = new ArrayList<>();

    public Repo(Class<T> clazz, SQL connection, ORMConfig config) throws ORMConfigurationException {
        this.info = new TableInfo(clazz, config);
        this.connection = connection;
    }

    public QueryBuilder<T> query(){
        return new QueryBuilder<>(this);
    }

    public QueryBuilder<T> where(String key, String op, Object value){
        return query().where(key, op, value);
    }

    public QueryBuilder<T> where(String key, Object value){
        return query().where(key, value);
    }

    public void save(T entry){
        if(entry.doesEntryExist()){
            update(entry);
        }else{
            create(entry);
        }
    }

    public void create(T entry){
        observers.forEach(o -> o.saving(entry));
        observers.forEach(o -> o.creating(entry));
        query().create(entry);
        observers.forEach(o -> o.created(entry));
        observers.forEach(o -> o.saved(entry));
    }

    public void update(T entry){
        observers.forEach(o -> o.saving(entry));
        observers.forEach(o -> o.updating(entry));
        where(info.getIdField(), getId(entry)).update(entry);
        observers.forEach(o -> o.updated(entry));
        observers.forEach(o -> o.saved(entry));
    }

    public void delete(T entry){
        observers.forEach(o -> o.deleting(entry));
        Timestamp timestamp = where(info.getIdField(), getId(entry)).delete();
        observers.forEach(o -> o.deleted(entry));
        if(timestamp != null){
            try {
                info.getField(info.getSoftDeleteField()).set(entry, timestamp);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void restore(T entry){
        if(!info.isSoftDelete())
            return;
        observers.forEach(o -> o.restoring(entry));
        where(info.getIdField(), getId(entry)).restore();
        observers.forEach(o -> o.restored(entry));
        try {
            info.getField(info.getSoftDeleteField()).set(entry, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void finalDelete(T entry){
        where(info.getIdField(), getId(entry)).finalDelete();
    }

    public T refresh(T entry){
        return where(info.getIdField(), getId(entry)).refresh(entry);
    }

    public T get(Object id){
        return where(info.getIdField(), id).get();
    }

    public List<T> all(){
        return query().all();
    }

    public Stream<T> stream(){
        return query().stream();
    }

    public int count(){
        return query().count();
    }

    private Object getId(T entry){
        if(entry == null)
            return null;
        try {
            Object id = info.getField(info.getIdField()).get(entry);
            if(id == null)
                return null;
            if(id.getClass().equals(Integer.class)){
                int intId = (Integer) id;
                if(intId == 0)
                    return null;
            }
            return id;
        } catch (IllegalAccessException e) {
            throw new ORMQueryException(e);
        }
    }

    public Repo<T> observe(Observer<T> observer){
        observers.add(observer);
        return this;
    }

    public void autoMigrate(){
        AutoMigrator.migrate(this);
    }

    public SQL getConnection(){
        return connection;
    }

    public TableInfo getInfo(){
        return info;
    }

}
