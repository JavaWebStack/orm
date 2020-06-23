package eu.bebendorf.ajorm;

import eu.bebendorf.ajorm.mapper.TypeMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder<T extends Model> {

    private final Repo<T> repository;
    private final TableInfo info;
    private final List<Condition> conditions = new ArrayList<>();
    private String order;
    private boolean desc;
    private boolean withDeleted = false;
    private int limit = -1;

    public QueryBuilder(Repo<T> repository){
        this.repository = repository;
        this.info = repository.getInfo();
    }

    private QueryBuilder<T> condition(String key, String op, Object value){
        for(TypeMapper mapper : repository.getInfo().getConfig().getTypeMappers())
            value = mapper.mapToSQL(value, repository.getInfo().getField(key).getType());
        conditions.add(new Condition(key, op, value));
        return this;
    }

    public QueryBuilder<T> where(String key, String op, Object value){
        if(value == null)
            return isNull(key);
        return condition(key, op, value);
    }

    public QueryBuilder<T> like(String key, Object value){
        if(value == null)
            return isNull(key);
        return where(key, "LIKE", value);
    }

    public QueryBuilder<T> where(String key, Object value){
        return where(key, "=", value);
    }

    public QueryBuilder<T> lessThan(String key, Object value){
        return where(key, "<", value);
    }

    public QueryBuilder<T> greaterThan(String key, Object value){
        return where(key, ">", value);
    }

    public QueryBuilder<T> isNull(String key){
        return condition(key, "IS NULL", null);
    }

    public QueryBuilder<T> notNull(String key){
        return condition(key, "IS NOT NULL", null);
    }

    public QueryBuilder<T> orderBy(String key, boolean desc){
        order = key;
        this.desc = desc;
        return this;
    }

    public QueryBuilder<T> orderBy(String key){
        return orderBy(key, false);
    }

    public QueryBuilder<T> limit(int limit){
        this.limit = limit;
        return this;
    }

    public QueryBuilder<T> withDeleted(){
        this.withDeleted = true;
        return this;
    }

    public T get(){
        List<T> all = all();
        if(all.size() == 0)
            return null;
        return all.get(0);
    }

    public List<T> all(){
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM `");
        sb.append(info.getTableName());
        sb.append("`");
        QueryPart where = makeWhere();
        sb.append(where.query);
        params.addAll(where.params);
        if(order != null){
            sb.append(" ORDER BY `");
            sb.append(info.getColumnName(order));
            sb.append("`");
            if(desc)
                sb.append(" DESC");
        }
        if(limit != -1) {
            sb.append(" LIMIT ");
            sb.append(limit);
        }
        sb.append(";");
        ResultSet rs = repository.getConnection().read(sb.toString(), params.toArray());
        List<T> results = parseResults(rs);
        repository.getConnection().close(rs);
        return results;
    }

    public T refresh(T entry){
        withDeleted = true;
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM `");
        sb.append(info.getTableName());
        sb.append("`");
        QueryPart where = makeWhere();
        sb.append(where.query);
        params.addAll(where.params);
        sb.append(";");
        ResultSet rs = repository.getConnection().read(sb.toString(), params.toArray());
        parseResult(rs, entry);
        repository.getConnection().close(rs);
        return entry;
    }

    public void finalDelete(){
        withDeleted = true;
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("DELETE FROM `");
        sb.append(info.getTableName());
        sb.append("`");
        QueryPart where = makeWhere();
        sb.append(where.query);
        params.addAll(where.params);
        sb.append(";");
        repository.getConnection().write(sb.toString(), params.toArray());
    }

    public Timestamp delete(){
        withDeleted = true;
        if(!info.isSoftDelete()) {
            finalDelete();
            return null;
        }
        List<Object> params = new ArrayList<>();
        Timestamp deletedAt = Timestamp.from(Instant.now());
        params.add(deletedAt);
        QueryPart part = makeWhere();
        params.addAll(part.params);
        repository.getConnection().write("UPDATE `"+info.getTableName()+"` SET `"+info.getColumnName(info.getSoftDeleteField())+"`=?"+part.query+";", params.toArray());
        return deletedAt;
    }

    public void restore(){
        withDeleted = true;
        if(!info.isSoftDelete())
            return;
        List<Object> params = new ArrayList<>();
        params.add(null);
        QueryPart part = makeWhere();
        params.addAll(part.params);
        repository.getConnection().write("UPDATE `"+info.getTableName()+"` SET `"+info.getColumnName(info.getSoftDeleteField())+"`=?"+part.query+";", params.toArray());
    }

    public void update(T entry){
        withDeleted = true;
        if(info.hasDates()){
            setValue(info.getUpdatedField(), entry, Timestamp.from(Instant.now()));
        }
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("UPDATE `");
        sb.append(info.getTableName());
        sb.append("` SET ");
        for(String fieldName : info.getFields()){
            if(fieldName.equals(info.getIdField()))
                continue;
            sb.append("`");
            sb.append(info.getColumnName(fieldName));
            sb.append("`=?");
            params.add(getValue(fieldName, entry));
        }
        QueryPart where = makeWhere();
        sb.append(where.query);
        params.addAll(where.params);
        sb.append(";");
        repository.getConnection().write(sb.toString(), params.toArray());
    }

    public void create(T entry){
        if(info.hasDates()){
            Timestamp now = Timestamp.from(Instant.now());
            setValue(info.getCreatedField(), entry, now);
            setValue(info.getUpdatedField(), entry, now);
        }
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("INSERT INTO `");
        sb.append(info.getTableName());
        sb.append("` (");
        List<String> cols = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for(String fieldName : info.getFields()){
            Object value = getValue(fieldName, entry);
            if(fieldName.equals(info.getIdField()) && info.isAutoIncrement() && ((Integer)value) < 1)
                continue;
            cols.add("`"+info.getColumnName(fieldName)+"`");
            values.add("?");
            params.add(value);
        }
        sb.append(String.join(",", cols));
        sb.append(") VALUES (");
        sb.append(String.join(",", values));
        sb.append(");");
        int id = repository.getConnection().write(sb.toString(), params.toArray());
        if(info.isAutoIncrement())
            setValue(info.getIdField(), entry, id);
    }

    private QueryPart makeWhere(){
        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();
        if(info.isSoftDelete() && !withDeleted)
            isNull(info.getSoftDeleteField());
        if(conditions.size() > 0){
            sb.append(" WHERE");
            for(Condition condition : conditions){
                sb.append(" `");
                sb.append(info.getColumnName(condition.fieldName));
                sb.append("` ");
                sb.append(condition.operator);
                if(condition.value != null){
                    sb.append(" ?");
                    params.add(condition.value);
                }
            }
        }
        return new QueryPart(sb.toString(), params);
    }

    private T parseResult(ResultSet rs, T t){
        for(String fieldName : info.getFields())
            setValue(fieldName, t, getValue(rs, info.getTargetType(fieldName), info.getColumnName(fieldName)));
        return t;
    }

    private List<T> parseResults(ResultSet rs){
        Class<? extends Model> model = info.getModelClass();
        List<T> list = new ArrayList<>();
        try {
            while (rs.next()){
                T t = (T) info.getModelConstructor().newInstance();
                list.add(parseResult(rs, t));
            }
        }catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException ex){
            ex.printStackTrace();
        }
        return list;
    }

    private Object getValue(String fieldName, T entry) {
        try {
            Object value = info.getField(fieldName).get(entry);
            for(TypeMapper mapper : info.getConfig().getTypeMappers())
                value = mapper.mapToSQL(value, info.getField(fieldName).getType());
            return value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getValue(ResultSet rs, Class<?> sqlType, String columnName) {
        try {
            if(sqlType.equals(String.class))
                return rs.getString(columnName);
            if(sqlType.equals(Integer.class))
                return rs.getInt(columnName);
            if(sqlType.equals(Long.class))
                return rs.getLong(columnName);
            if(sqlType.equals(Double.class))
                return rs.getDouble(columnName);
            if(sqlType.equals(Timestamp.class))
                return rs.getTimestamp(columnName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setValue(String fieldName, T entry, Object value) {
        try {
            for(TypeMapper mapper : repository.getInfo().getConfig().getTypeMappers())
                value = mapper.mapToJava(value, repository.getInfo().getField(fieldName).getType());
            repository.getInfo().getField(fieldName).set(entry, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private class Condition {
        String fieldName;
        String operator;
        Object value;
        public Condition(String fieldName, String operator, Object value){
            this.fieldName = fieldName;
            this.operator = operator;
            this.value = value;
        }
    }

    private class QueryPart {
        String query;
        List<Object> params;
        public QueryPart(String query, List<Object> params) {
            this.query = query;
            this.params = params;
        }
    }

}
