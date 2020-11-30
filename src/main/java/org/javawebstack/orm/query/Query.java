package org.javawebstack.orm.query;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.SQLMapper;
import org.javawebstack.orm.exception.ORMQueryException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class Query<T extends Model> {

    private final Repo<T> repo;
    private final Class<T> model;
    private final QueryGroup<T> where;
    private Integer offset;
    private Integer limit;
    private String order;
    private boolean desc = false;
    private boolean withDeleted = false;
    private final Map<Class<? extends Model>, QueryCondition> leftJoins = new HashMap<>();

    public Query(Class<T> model){
        this(Repo.get(model), model);
    }

    public Query(Repo<T> repo, Class<T> model){
        this.repo = repo;
        this.model = model;
        this.where = new QueryGroup<>();
    }

    public Class<T> getModel() {
        return model;
    }

    public Query<T> leftJoin(Class<? extends Model> model, String self, String other){
        leftJoins.put(model, new QueryCondition(new QueryColumn(repo.getInfo().getTableName()+"."+self), "=", new QueryColumn(Repo.get(model).getInfo().getTableName()+"."+other)));
        return this;
    }

    public Query<T> and(Function<QueryGroup<T>,QueryGroup<T>> group){
        where.and(group);
        return this;
    }

    public Query<T> or(Function<QueryGroup<T>,QueryGroup<T>> group){
        where.or(group);
        return this;
    }

    public Query<T> where(Object left, String condition, Object right){
        where.where(left, condition, right);
        return this;
    }

    public Query<T> where(Object left, Object right){
        where.where(left, right);
        return this;
    }

    public Query<T> where(Class<? extends Model> leftTable, String left, String operator, Class<? extends Model> rightTable, String right){
        if(rightTable != null)
            right = Repo.get(rightTable).getInfo().getTableName() + "." + right;
        return where(leftTable, left, operator, new QueryColumn(right));
    }

    public Query<T> where(Class<? extends Model> leftTable, String left, String operator, Object right){
        if(leftTable != null)
            left = Repo.get(leftTable).getInfo().getTableName() + "." + left;
        return where(left, operator, right);
    }

    public QueryGroup<T> whereMorph(String name, Class<? extends Model> type){
        return where.whereMorph(name, type);
    }

    public QueryGroup<T> whereMorph(String name, Class<? extends Model> type, Object id){
        return where.whereMorph(name, type, id);
    }

    public QueryGroup<T> whereMorph(String name, Model entity){
        return where.whereMorph(name, entity);
    }

    public Query<T> orWhere(Class<? extends Model> leftTable, String left, String operator, Class<? extends Model> rightTable, String right){
        if(rightTable != null)
            right = Repo.get(rightTable).getInfo().getTableName() + "." + right;
        return orWhere(leftTable, left, operator, new QueryColumn(right));
    }

    public Query<T> orWhere(Class<? extends Model> leftTable, String left, String operator, Object right){
        if(leftTable != null)
            left = Repo.get(leftTable).getInfo().getTableName() + "." + left;
        return orWhere(left, operator, right);
    }

    public QueryGroup<T> orWhereMorph(String name, Class<? extends Model> type){
        return where.orWhereMorph(name, type);
    }

    public QueryGroup<T> orWhereMorph(String name, Class<? extends Model> type, Object id){
        return where.orWhereMorph(name, type, id);
    }

    public QueryGroup<T> orWhereMorph(String name, Model entity){
        return where.orWhereMorph(name, entity);
    }

    public Query<T> whereId(String operator, Object right){
        return where(repo.getInfo().getIdField(), operator, right);
    }

    public Query<T> whereId(Object right){
        return whereId("=", right);
    }

    public Query<T> orWhereId(String operator, Object right){
        return orWhere(repo.getInfo().getIdField(), operator, right);
    }

    public Query<T> orWhereId(Object right){
        return orWhereId("=", right);
    }

    public Query<T> isNull(Object left){
        where.isNull(left);
        return this;
    }

    public Query<T> notNull(Object left){
        where.notNull(left);
        return this;
    }

    public Query<T> lessThan(Object left, Object right){
        where.lessThan(left, right);
        return this;
    }

    public Query<T> greaterThan(Object left, Object right){
        where.greaterThan(left, right);
        return this;
    }

    public Query<T> orWhere(Object left, String condition, Object right){
        where.orWhere(left, condition, right);
        return this;
    }

    public Query<T> orWhere(Object left, Object right){
        where.orWhere(left, right);
        return this;
    }

    public Query<T> orIsNull(Object left){
        where.orIsNull(left);
        return this;
    }

    public Query<T> orNotNull(Object left){
        where.orNotNull(left);
        return this;
    }

    public Query<T> orLessThan(Object left, Object right){
        where.orLessThan(left, right);
        return this;
    }

    public Query<T> orGreaterThan(Object left, Object right){
        where.orGreaterThan(left, right);
        return this;
    }

    public <M extends Model> Query<T> whereExists(Class<M> model, Function<Query<M>,Query<M>> consumer){
        where.whereExists(model, consumer);
        return this;
    }

    public <M extends Model> Query<T> orWhereExists(Class<M> model, Function<Query<M>,Query<M>> consumer){
        where.orWhereExists(model, consumer);
        return this;
    }

    public Query<T> order(String orderBy, boolean desc){
        this.order = orderBy;
        this.desc = desc;
        return this;
    }

    public Query<T> order(String orderBy){
        return order(orderBy, false);
    }

    public Query<T> limit(int offset, int limit){
        return offset(offset).limit(limit);
    }

    public Query<T> limit(int limit){
        this.limit = limit;
        return this;
    }

    public Query<T> offset(int offset){
        this.offset = offset;
        return this;
    }

    public Query<T> withDeleted(){
        withDeleted = true;
        return this;
    }

    public QueryString getQueryString(){
        return getQueryString(false);
    }

    public QueryString getQueryString(boolean count) {
        List<Object> parameters = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT ")
                .append(count ? "COUNT(*)" : "*")
                .append(" FROM `")
                .append(repo.getInfo().getTableName())
                .append('`');
        for(Class<? extends Model> type : leftJoins.keySet()){
            sb.append(" LEFT JOIN `")
                    .append(Repo.get(type).getInfo().getTableName())
                    .append("` ON ")
                    .append(leftJoins.get(type).getQueryString(repo.getInfo()).getQuery());
        }
        considerSoftDelete();
        if(where.getQueryElements().size() > 0){
            QueryString qs = where.getQueryString(repo.getInfo());
            sb.append(" WHERE ").append(qs.getQuery());
            parameters.addAll(qs.getParameters());
        }
        if(order != null){
            sb.append(" ORDER BY `").append(repo.getInfo().getColumnName(order)).append('`');
            if(desc)
                sb.append(" DESC");
        }
        if(offset != null && limit == null)
            limit = Integer.MAX_VALUE;
        if(limit != null){
            sb.append(" LIMIT ?");
            if(offset != null){
                sb.append(",?");
                parameters.add(offset);
            }
            parameters.add(limit);
        }
        return new QueryString(sb.toString(), SQLMapper.mapParams(repo, parameters));
    }

    public void finalDelete(){
        List<Object> parameters = new ArrayList<>();
        StringBuilder sb = new StringBuilder("DELETE FROM `")
                .append(repo.getInfo().getTableName())
                .append('`');
        if(where.getQueryElements().size() > 0){
            QueryString qs = where.getQueryString(repo.getInfo());
            sb.append(" WHERE ").append(qs.getQuery());
            parameters = qs.getParameters();
        }
        try {
            repo.getConnection().write(sb.toString(), SQLMapper.mapParams(repo, parameters).toArray());
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

    public Timestamp delete(){
        if(!repo.getInfo().isSoftDelete()) {
            finalDelete();
            return null;
        }
        Timestamp now = Timestamp.from(Instant.now());
        Map<String, Object> values = new HashMap<>();
        values.put(repo.getInfo().getColumnName(repo.getInfo().getSoftDeleteField()), now);
        update(values);
        return now;
    }

    public void restore(){
        if(!repo.getInfo().isSoftDelete())
            return;
        Map<String, Object> values = new HashMap<>();
        values.put(repo.getInfo().getColumnName(repo.getInfo().getSoftDeleteField()), null);
        withDeleted().update(values);
    }

    private void considerSoftDelete(){
        if(repo.getInfo().isSoftDelete() && !withDeleted){
            if(where.getQueryElements().size() > 0)
                where.getQueryElements().add(0, QueryConjunction.AND);
            where.getQueryElements().add(0, new QueryCondition(new QueryColumn(repo.getInfo().getColumnName(repo.getInfo().getSoftDeleteField())) ,"IS NULL", null));
        }
    }

    public T refresh(T entity){
        QueryString qs = getQueryString(false);
        try {
            ResultSet rs = repo.getConnection().read(qs.getQuery(), SQLMapper.mapParams(repo, SQLMapper.mapParams(repo, qs.getParameters())).toArray());
            SQLMapper.mapBack(repo, rs, entity);
            repo.getConnection().close(rs);
            return entity;
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

    public void update(T entity){
        update(SQLMapper.map(repo, entity));
    }

    public void update(Map<String, Object> values){
        if(repo.getInfo().hasUpdated())
            values.put(repo.getInfo().getColumnName(repo.getInfo().getUpdatedField()), Timestamp.from(Instant.now()));
        List<Object> parameters = new ArrayList<>();
        List<String> sets = new ArrayList<>();
        values.forEach((key, value) -> {
            sets.add("`"+key+"`=?");
            parameters.add(value);
        });
        StringBuilder sb = new StringBuilder("UPDATE `")
                .append(repo.getInfo().getTableName())
                .append("` SET ")
                .append(String.join(",", sets));
        considerSoftDelete();
        if(where.getQueryElements().size() > 0){
            QueryString qs = where.getQueryString(repo.getInfo());
            sb.append(" WHERE ").append(qs.getQuery());
            parameters.addAll(qs.getParameters());
        }
        sb.append(';');
        try {
            repo.getConnection().write(sb.toString(), SQLMapper.mapParams(repo, parameters).toArray());
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

    public List<T> all(){
        QueryString qs = getQueryString(false);
        try {
            ResultSet rs = repo.getConnection().read(qs.getQuery(), SQLMapper.mapParams(repo, qs.getParameters()).toArray());
            List<Class<? extends Model>> joinedModels = new ArrayList<>();
            joinedModels.addAll(leftJoins.keySet());
            List<T> list = SQLMapper.map(repo, rs, joinedModels);
            repo.getConnection().close(rs);
            return list;
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

    public T get(){
        List<T> list = limit(1).all();
        if(list.size() == 0)
            return null;
        return list.get(0);
    }

    public Stream<T> stream(){
        return all().stream();
    }

    public int count(){
        QueryString qs = getQueryString(true);
        try {
            ResultSet rs = repo.getConnection().read(qs.getQuery(), SQLMapper.mapParams(repo, qs.getParameters()).toArray());
            int c = 0;
            if(rs.next())
                c = rs.getInt(1);
            repo.getConnection().close(rs);
            return c;
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

    public boolean hasRecords(){
        return count() > 0;
    }

}
