package org.javawebstack.orm.query;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.SQLMapper;
import org.javawebstack.orm.Session;
import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.wrapper.SQL;
import org.javawebstack.orm.wrapper.builder.SQLQueryString;

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
    private SQL connection;
    private List<String> select = new ArrayList<>();
    private final QueryGroup<T> where = new QueryGroup<>();
    private Integer offset;
    private Integer limit;
    private final List<QueryOrderBy> order = new ArrayList<>();
    private boolean withDeleted = false;
    private final List<QueryColumn> groupBy = new ArrayList<>();
    private QueryGroup<T> having;

    public Query(Class<T> model) {
        this(Repo.get(model), model);
    }

    public Query(Repo<T> repo, Class<T> model) {
        this.repo = repo;
        this.model = model;
        Session session = Session.current();
        this.connection = repo.getConnection();
        if(session != null && session.getConnection() != null)
            this.connection = session.getConnection();
    }

    public Query<T> via(SQL connection) {
        this.connection = connection;
        return this;
    }

    public boolean isWithDeleted() {
        return withDeleted;
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
        this.select = Arrays.asList(columns);
        return this;
    }

    public Query<T> and(Function<QueryGroup<T>, QueryGroup<T>> group) {
        where.and(group);
        return this;
    }

    public Query<T> or(Function<QueryGroup<T>, QueryGroup<T>> group) {
        where.or(group);
        return this;
    }

    public Query<T> where(Object left, String condition, Object right) {
        where.where(left, condition, right);
        return this;
    }

    public Query<T> where(Object left, Object right) {
        where.where(left, right);
        return this;
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

    public QueryGroup<T> whereMorph(String name, Class<? extends Model> type) {
        return where.whereMorph(name, type);
    }

    public QueryGroup<T> whereMorph(String name, Class<? extends Model> type, Object id) {
        return where.whereMorph(name, type, id);
    }

    public QueryGroup<T> whereMorph(String name, Model entity) {
        return where.whereMorph(name, entity);
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

    public QueryGroup<T> orWhereMorph(String name, Class<? extends Model> type) {
        return where.orWhereMorph(name, type);
    }

    public QueryGroup<T> orWhereMorph(String name, Class<? extends Model> type, Object id) {
        return where.orWhereMorph(name, type, id);
    }

    public QueryGroup<T> orWhereMorph(String name, Model entity) {
        return where.orWhereMorph(name, entity);
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

    public Query<T> isNull(Object left) {
        where.isNull(left);
        return this;
    }

    public Query<T> notNull(Object left) {
        where.notNull(left);
        return this;
    }
    
    public Query<T> whereNull(Object left) {
        where.isNull(left);
        return this;
    }
    
    public Query<T> whereNotNull(Object left) {
        where.notNull(left);
        return this;
    }

    public Query<T> lessThan(Object left, Object right) {
        where.lessThan(left, right);
        return this;
    }

    public Query<T> greaterThan(Object left, Object right) {
        where.greaterThan(left, right);
        return this;
    }

    public Query<T> orWhere(Object left, String condition, Object right) {
        where.orWhere(left, condition, right);
        return this;
    }

    public Query<T> orWhere(Object left, Object right) {
        where.orWhere(left, right);
        return this;
    }

    public Query<T> orIsNull(Object left) {
        where.orIsNull(left);
        return this;
    }

    public Query<T> orNotNull(Object left) {
        where.orNotNull(left);
        return this;
    }

    public Query<T> orLessThan(Object left, Object right) {
        where.orLessThan(left, right);
        return this;
    }

    public Query<T> orGreaterThan(Object left, Object right) {
        where.orGreaterThan(left, right);
        return this;
    }

    public <M extends Model> Query<T> whereExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        where.whereExists(model, consumer);
        return this;
    }

    public <M extends Model> Query<T> orWhereExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        where.orWhereExists(model, consumer);
        return this;
    }

    public Query<T> whereIn(Object left, Object... values) {
        where.whereIn(left, values);
        return this;
    }

    public Query<T> whereNotIn(Object left, Object... values) {
        where.whereNotIn(left, values);
        return this;
    }

    public Query<T> orWhereIn(Object left, Object... values) {
        where.orWhereIn(left, values);
        return this;
    }

    public Query<T> orWhereNotIn(Object left, Object... values) {
        where.orWhereNotIn(left, values);
        return this;
    }

    public Query<T> groupBy(String column) {
        groupBy.add(new QueryColumn(column));
        return this;
    }

    public Query<T> groupBy(QueryColumn column) {
        groupBy.add(column);
        return this;
    }

    public Query<T> having(Consumer<QueryGroup<T>> consumer) {
        having = new QueryGroup<>();
        consumer.accept(having);
        return this;
    }

    public Query<T> has(Query<?> relation, String operator, int count) {
        where.has(relation, operator, count);
        return this;
    }

    public Query<T> has(Query<?> relation) {
        return has(relation, ">=", 1);
    }

    public Query<T> accessible(Object accessor) {
        return repo.accessible(this, accessor);
    }

    public Query<T> filter(Map<String, String> filter) {
        if (filter == null)
            return this;
        repo.getFilter().filter(this, filter);
        return this;
    }

    public Query<T> search(String search) {
        if (search == null || search.length() == 0)
            return this;
        repo.getFilter().search(this, search);
        return this;
    }

    public Query<T> order(String columnName) throws ORMQueryException {
        return order(columnName, false);
    }

    public Query<T> order(String columnName, boolean desc) throws ORMQueryException {
        return order(new QueryColumn(columnName), desc);
    }

    public Query<T> order(QueryColumn column, boolean desc) throws ORMQueryException{
        this.order.add(new QueryOrderBy(column, desc));
        return this;
    }

    public Query<T> limit(int offset, int limit) {
        return offset(offset).limit(limit);
    }

    public Query<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Query<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public Query<T> withDeleted() {
        withDeleted = true;
        return this;
    }

    public void finalDelete() {
        SQLQueryString qs = connection.builder().buildDelete(this);
        try {
            connection.write(qs.getQuery(), qs.getParameters().toArray());
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
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
        withDeleted().update(values);
    }

    public T refresh(T entity) {
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

    public void update(T entity) {
        update(SQLMapper.map(repo, entity));
    }

    public void update(Map<String, Object> values) {
        SQLQueryString queryString = connection.builder().buildUpdate(this, values);
        try {
            connection.write(queryString.getQuery(), queryString.getParameters().toArray());
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

    public List<T> all() {
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

    public boolean hasRecords() {
        return count() > 0;
    }

}
