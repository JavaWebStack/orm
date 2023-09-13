package org.javawebstack.orm.query;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.SQLMapper;
import org.javawebstack.orm.connection.pool.PooledSQL;
import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.renderer.SQLQueryString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class Query<T extends Model> {

    private final Repo<T> repo;
    private final Class<T> model;
    private List<String> select = new ArrayList<>();
    private QueryGroup<T> where;
    private Integer offset;
    private Integer limit;
    private final List<QueryOrderBy> order = new ArrayList<>();
    private boolean withDeleted = false;
    private final List<QueryColumn> groupBy = new ArrayList<>();
    private QueryGroup<T> having;
    private boolean applyAccessible = false;
    private Object accessor;
    private boolean immutable;

    public Query(Class<T> model) {
        this(Repo.get(model), model);
    }

    public Query(Class<T> model, boolean immutable) {
        this(Repo.get(model), model, immutable);
    }

    public Query(Repo<T> repo, Class<T> model) {
        this(repo, model, false);
    }

    public Query(Repo<T> repo, Class<T> model, boolean immutable) {
        this.repo = repo;
        this.model = model;
        this.immutable = immutable;
        this.where = new QueryGroup<>(immutable);
    }

    public Query<T> clone() {
        Query<T> cloned = new Query<>(repo, model, immutable);
        cloned.select.addAll(select);
        cloned.where = where.clone();
        cloned.offset = offset;
        cloned.limit = limit;
        cloned.order.addAll(order);
        cloned.withDeleted = withDeleted;
        cloned.groupBy.addAll(groupBy);
        cloned.having = having.clone();
        cloned.applyAccessible = applyAccessible;
        cloned.accessor = accessor;
        return cloned;
    }

    public Query<T> mutable() {
        if(!immutable)
            return this;
        Query<T> cloned = clone();
        cloned.immutable = false;
        return cloned;
    }

    public Query<T> immutable() {
        if(immutable)
            return this;
        Query<T> cloned = clone();
        cloned.immutable = true;
        return cloned;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public boolean isWithDeleted() {
        return withDeleted;
    }

    public boolean shouldApplyAccessible() {
        return applyAccessible;
    }

    public Object getAccessor() {
        return accessor;
    }

    public QueryGroup<T> getWhereGroup() {
        return where;
    }

    public List<String> getSelect() {
        return select;
    }

    public List<QueryColumn> getGroupBy() {
        return groupBy;
    }

    public QueryGroup<T> getHaving() {
        return having;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public List<QueryOrderBy> getOrder() {
        return order;
    }

    public Repo<T> getRepo() {
        return repo;
    }

    public Class<T> getModel() {
        return model;
    }

    public Query<T> select(String... columns) {
        Query<T> q = immutable ? clone() : this;
        q.select = Arrays.asList(columns);
        return q;
    }

    public Query<T> and(Function<QueryGroup<T>, QueryGroup<T>> group) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.and(group);
        return q;
    }

    public Query<T> or(Function<QueryGroup<T>, QueryGroup<T>> group) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.or(group);
        return q;
    }

    public Query<T> where(Object left, String condition, Object right) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.where(left, condition, right);
        return q;
    }

    public Query<T> where(Object left, Object right) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.where(left, right);
        return q;
    }

    public Query<T> where(Class<? extends Model> leftTable, String left, String operator, Class<? extends Model> rightTable, String right) {
        if (rightTable != null)
            right = Repo.get(rightTable).getInfo().getTableName() + "." + Repo.get(rightTable).getInfo().getColumnName(right);
        return where(leftTable, left, operator, new QueryColumn(right));
    }

    public Query<T> where(Class<? extends Model> leftTable, String left, String operator, Object right) {
        if (leftTable != null)
            left = Repo.get(leftTable).getInfo().getTableName() + "." + Repo.get(leftTable).getInfo().getColumnName(left);
        return where(left, operator, right);
    }

    public Query<T> like(String left, Object right) {
        return where(left, "LIKE", right);
    }

    public Query<T> orLike(String left, Object right) {
        return orWhere(left, "LIKE", right);
    }

    public Query<T> orWhere(Class<? extends Model> leftTable, String left, String operator, Class<? extends Model> rightTable, String right) {
        if (rightTable != null)
            right = Repo.get(rightTable).getInfo().getTableName() + "." + Repo.get(rightTable).getInfo().getColumnName(right);
        return orWhere(leftTable, left, operator, new QueryColumn(right));
    }

    public Query<T> orWhere(Class<? extends Model> leftTable, String left, String operator, Object right) {
        if (leftTable != null)
            left = Repo.get(leftTable).getInfo().getTableName() + "." + Repo.get(leftTable).getInfo().getColumnName(left);
        return orWhere(left, operator, right);
    }

    public Query<T> orWhereMorph(String name, Class<? extends Model> type) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhereMorph(name, type);
        return q;
    }

    public Query<T> orWhereMorph(String name, Class<? extends Model> type, Object id) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhereMorph(name, type, id);
        return q;
    }

    public Query<T> orWhereMorph(String name, Model entity) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhereMorph(name, entity);
        return q;
    }

    public Query<T> whereId(String operator, Object right) {
        return where(repo.getInfo().getIdField(), operator, right);
    }

    public Query<T> whereId(Object right) {
        return whereId("=", right);
    }

    public Query<T> whereId(Class<? extends Model> other, String field) {
        return whereId("=", other, field);
    }

    public Query<T> whereId(String operator, Class<? extends Model> other, String field) {
        return whereId(operator, new QueryColumn(Repo.get(other).getInfo().getTableName() + "." + Repo.get(other).getInfo().getColumnName(field)));
    }

    public Query<T> orWhereId(String operator, Object right) {
        return orWhere(repo.getInfo().getIdField(), operator, right);
    }

    public Query<T> orWhereId(Object right) {
        return orWhereId("=", right);
    }

    public Query<T> orWhereId(Class<? extends Model> other, String field) {
        return orWhereId("=", other, field);
    }

    public Query<T> orWhereId(String operator, Class<? extends Model> other, String field) {
        return orWhereId(operator, new QueryColumn(Repo.get(other).getInfo().getTableName() + "." + Repo.get(other).getInfo().getColumnName(field)));
    }

    @Deprecated
    public Query<T> isNull(Object left) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.isNull(left);
        return q;
    }

    @Deprecated
    public Query<T> notNull(Object left) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.notNull(left);
        return q;
    }
    
    public Query<T> whereNull(Object left) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.whereNull(left);
        return q;
    }
    
    public Query<T> whereNotNull(Object left) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.whereNotNull(left);
        return q;
    }

    public Query<T> orWhere(Object left, String condition, Object right) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhere(left, condition, right);
        return q;
    }

    public Query<T> orWhere(Object left, Object right) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhere(left, right);
        return q;
    }

    @Deprecated
    public Query<T> orIsNull(Object left) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orIsNull(left);
        return this;
    }

    @Deprecated
    public Query<T> orNotNull(Object left) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orNotNull(left);
        return this;
    }

    public Query<T> orWhereNull(Object left) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhereNull(left);
        return this;
    }

    public Query<T> orWhereNotNull(Object left) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhereNotNull(left);
        return this;
    }

    public <M extends Model> Query<T> whereExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.whereExists(model, consumer);
        return this;
    }

    public <M extends Model> Query<T> orWhereExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhereExists(model, consumer);
        return this;
    }

    public Query<T> whereIn(Object left, Object... values) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.whereIn(left, values);
        return this;
    }

    public Query<T> whereNotIn(Object left, Object... values) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.whereNotIn(left, values);
        return q;
    }

    public Query<T> orWhereIn(Object left, Object... values) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhereIn(left, values);
        return q;
    }

    public Query<T> orWhereNotIn(Object left, Object... values) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.orWhereNotIn(left, values);
        return this;
    }

    public Query<T> groupBy(String column) {
        Query<T> q = immutable ? clone() : this;
        q.groupBy.add(new QueryColumn(column));
        return this;
    }

    public Query<T> groupBy(QueryColumn column) {
        Query<T> q = immutable ? clone() : this;
        q.groupBy.add(column);
        return q;
    }

    public Query<T> having(Consumer<QueryGroup<T>> consumer) {
        Query<T> q = immutable ? clone() : this;
        q.having = new QueryGroup<>(immutable);
        consumer.accept(having);
        return q;
    }

    public Query<T> has(Query<?> relation, String operator, int count) {
        Query<T> q = immutable ? clone() : this;
        q.where = q.where.has(relation, operator, count);
        return q;
    }

    public Query<T> has(Query<?> relation) {
        return has(relation, ">=", 1);
    }

    public Query<T> accessible(Object accessor) {
        Query<T> q = immutable ? clone() : this;
        q.applyAccessible = true;
        q.accessor = accessor;
        return q;
    }

    public Query<T> filter(Map<String, String> filter) {
        Query<T> q = immutable ? clone() : this;
        if (filter == null)
            return q;
        q = q.mutable();
        repo.getFilter().filter(q, filter);
        return immutable ? q.immutable() : q;
    }

    public Query<T> search(String search) {
        Query<T> q = immutable ? clone() : this;
        if (search == null || search.length() == 0)
            return this;
        q = q.mutable();
        repo.getFilter().search(q, search);
        return immutable ? q.immutable() : q;
    }

    public Query<T> order(String columnName) throws ORMQueryException {
        return order(columnName, false);
    }

    public Query<T> order(String columnName, boolean desc) throws ORMQueryException {
        return order(new QueryColumn(columnName), desc);
    }

    public Query<T> order(QueryColumn column, boolean desc) throws ORMQueryException {
        Query<T> q = immutable ? clone() : this;
        q.order.add(new QueryOrderBy(column, desc));
        return q;
    }

    public Query<T> limit(int offset, int limit) {
        return offset(offset).limit(limit);
    }

    public Query<T> limit(int limit) {
        Query<T> q = immutable ? clone() : this;
        q.limit = limit;
        return q;
    }

    public Query<T> offset(int offset) {
        Query<T> q = immutable ? clone() : this;
        q.offset = offset;
        return q;
    }

    public Query<T> withDeleted() {
        Query<T> q = immutable ? clone() : this;
        q.withDeleted = true;
        return q;
    }

    public Query<T> onlyDeleted() {
        return withDeleted().whereNotNull(repo.getInfo().getSoftDeleteField());
    }

    public void finalDelete() {
        try(PooledSQL connection = repo.getPool().get()) {
            SQLQueryString qs = connection.builder().buildDelete(this);
            try {
                connection.write(qs.getQuery(), qs.getParameters().toArray());
            } catch (SQLException throwables) {
                throw new ORMQueryException(throwables);
            }
        }
    }

    public Timestamp delete() {
        if (!repo.getInfo().isSoftDelete()) {
            finalDelete();
            return null;
        }
        Timestamp now = Timestamp.from(Instant.now());
        Map<String, Object> values = new HashMap<>();
        values.put(repo.getInfo().getColumnName(repo.getInfo().getSoftDeleteField()), now);
        update(values);
        return now;
    }

    public void restore() {
        if (!repo.getInfo().isSoftDelete())
            return;
        Map<String, Object> values = new HashMap<>();
        values.put(repo.getInfo().getColumnName(repo.getInfo().getSoftDeleteField()), null);
        clone().withDeleted().update(values);
    }

    public T refresh(T entity) {
        try(PooledSQL connection = repo.getPool().get()) {
            SQLQueryString qs = connection.builder().buildQuery(this);
            try {
                ResultSet rs = connection.read(qs.getQuery(), qs.getParameters().toArray());
                if(rs.next()) {
                    SQLMapper.mapBack(repo, rs, entity);
                    connection.close(rs);
                    return entity;
                } else {
                    return null;
                }
            } catch (SQLException throwables) {
                throw new ORMQueryException(throwables);
            }
        }
    }

    public void update(T entity) {
        update(SQLMapper.map(repo, entity));
    }

    public void update(Map<String, Object> values) {
        try(PooledSQL connection = repo.getPool().get()) {
            SQLQueryString queryString = connection.builder().buildUpdate(this, values);
            try {
                connection.write(queryString.getQuery(), queryString.getParameters().toArray());
            } catch (SQLException throwables) {
                throw new ORMQueryException(throwables);
            }
        }
    }

    public List<T> all() {
        try(PooledSQL connection = repo.getPool().get()) {
            SQLQueryString qs = connection.builder().buildQuery(this);
            try {
                ResultSet rs = connection.read(qs.getQuery(), qs.getParameters().toArray());
                List<T> list = SQLMapper.map(repo, rs, new ArrayList<>());
                connection.close(rs);
                return list;
            } catch (SQLException throwables) {
                throw new ORMQueryException(throwables);
            }
        }
    }

    public List<T> get() {
        return all();
    }

    public T first() {
        List<T> list = limit(1).all();
        if (list.size() == 0)
            return null;
        return list.get(0);
    }

    public Stream<T> stream() {
        return all().stream();
    }

    public int count() {
        try(PooledSQL connection = repo.getPool().get()) {
            SQLQueryString qs = connection.builder().buildQuery(this.select("count(*)"));
            try {
                ResultSet rs = connection.read(qs.getQuery(), qs.getParameters().toArray());
                int c = 0;
                if (rs.next())
                    c = rs.getInt(1);
                connection.close(rs);
                return c;
            } catch (SQLException throwables) {
                throw new ORMQueryException(throwables);
            }
        }
    }

    @Deprecated
    public boolean hasRecords() {
        return isNotEmpty();
    }

    public boolean isNotEmpty() {
        return limit(1).count() > 0;
    }

    public boolean isEmpty() {
        return !isNotEmpty();
    }

}
