package org.javawebstack.orm;

import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.migration.AutoMigrator;
import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.wrapper.SQL;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class Repo<T extends Model> {

    public static <T extends Model> Repo<T> get(Class<T> model){
        return ORM.repo(model);
    }

    private final TableInfo info;
    private final SQL connection;
    private final List<Observer<T>> observers = new ArrayList<>();
    private Accessible<T> accessible;

    public Repo(Class<T> clazz, SQL connection, ORMConfig config) throws ORMConfigurationException {
        this.info = new TableInfo(clazz, config);
        this.connection = connection;
    }

    public Repo<T> setAccessible(Accessible<T> accessible){
        this.accessible = accessible;
        return this;
    }

    public Query<T> query(){
        return new Query<>((Class<T>) info.getModelClass());
    }

    public Query<T> where(Object left, String operator, Object right){
        return query().where(left, operator, right);
    }

    public Query<T> where(Object left, Object right){
        return query().where(left, right);
    }

    public Query<T> whereId(String operator, Object right){
        return query().whereId(operator, right);
    }

    public Query<T> whereId(Object right){
        return query().whereId(right);
    }

    public <M extends Model> Query<T> whereExists(Class<M> model, Function<Query<M>,Query<M>> consumer){
        return query().whereExists(model, consumer);
    }

    public Query<T> accessible(Object accessor){
        return accessible(query(), accessor);
    }

    public Query<T> accessible(Query<T> query, Object accessor){
        return accessible == null ? query : accessible.access(query, accessor);
    }

    public void save(T entry){
        if(entry.doesEntryExist()){
            update(entry);
        }else{
            create(entry);
        }
    }

    public void create(T entry){
        if(info.getConfig().getInjector() != null)
            info.getConfig().getInjector().inject(entry);
        observers.forEach(o -> o.saving(entry));
        observers.forEach(o -> o.creating(entry));
        executeCreate(entry);
        observers.forEach(o -> o.created(entry));
        observers.forEach(o -> o.saved(entry));
    }

    private void executeCreate(T entry){
        try {
            if(info.hasDates()){
                Timestamp now = Timestamp.from(Instant.now());
                if(info.hasCreated())
                    info.getField(info.getCreatedField()).set(entry, now);
                if(info.hasUpdated())
                    info.getField(info.getUpdatedField()).set(entry, now);
            }
            if(info.getIdType().equals(UUID.class)){
                Field field = info.getField(info.getIdField());
                if(field.get(entry) == null)
                    field.set(entry, UUID.randomUUID());
            }
            List<Object> params = new ArrayList<>();
            StringBuilder sb = new StringBuilder("INSERT INTO `");
            sb.append(info.getTableName());
            sb.append("` (");
            List<String> cols = new ArrayList<>();
            List<String> values = new ArrayList<>();
            Map<String, Object> map = SQLMapper.map(this, entry);
            if(info.isAutoIncrement()){
                String idCol = info.getColumnName(info.getIdField());
                if(map.containsKey(idCol) && map.get(idCol) == null)
                    map.remove(idCol);
            }
            for(String columnName : map.keySet()){
                cols.add("`"+columnName+"`");
                values.add("?");
                params.add(map.get(columnName));
            }
            sb.append(String.join(",", cols));
            sb.append(") VALUES (");
            sb.append(String.join(",", values));
            sb.append(");");
            int id = connection.write(sb.toString(), params.toArray());
            if(info.isAutoIncrement())
                info.getField(info.getIdField()).set(entry, id);
            entry.setEntryExists(true);
        } catch (SQLException | IllegalAccessException throwables) {
            throw new ORMQueryException(throwables);
        }
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
        if(timestamp != null){
            try {
                info.getField(info.getSoftDeleteField()).set(entry, timestamp);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        observers.forEach(o -> o.deleted(entry));
    }

    public void restore(T entry){
        if(!info.isSoftDelete())
            return;
        observers.forEach(o -> o.restoring(entry));
        where(info.getIdField(), getId(entry)).restore();
        try {
            info.getField(info.getSoftDeleteField()).set(entry, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        observers.forEach(o -> o.restored(entry));
    }

    public void finalDelete(T entry){
        where(info.getIdField(), getId(entry)).finalDelete();
    }

    public T refresh(T entry){
        return where(info.getIdField(), getId(entry)).refresh(entry);
    }

    public T get(Object id){
        return whereId(id).get();
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

    public Object getId(Object entity){
        if(entity == null)
            return null;
        try {
            Object id = info.getField(info.getIdField()).get(entity);
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
        if(info.getConfig().getInjector() != null)
            info.getConfig().getInjector().inject(observer);
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
