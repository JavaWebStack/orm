package org.javawebstack.orm.query;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.TableInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class QueryGroup<T extends Model> implements QueryElement {

    private final List<QueryElement> queryElements = new ArrayList<>();

    public QueryGroup(QueryElement... queryElements) {
        this.queryElements.addAll(Arrays.asList(queryElements));
    }

    public List<QueryElement> getQueryElements() {
        return queryElements;
    }

    public QueryGroup<T> and(Function<QueryGroup<T>, QueryGroup<T>> group) {
        QueryGroup<T> innerGroup = group.apply(new QueryGroup<>());
        if (queryElements.size() > 0)
            queryElements.add(QueryConjunction.AND);
        queryElements.add(innerGroup);
        return this;
    }

    public QueryGroup<T> or(Function<QueryGroup<T>, QueryGroup<T>> group) {
        QueryGroup<T> innerGroup = group.apply(new QueryGroup<>());
        if (queryElements.size() > 0)
            queryElements.add(QueryConjunction.OR);
        queryElements.add(innerGroup);
        return this;
    }

    public QueryGroup<T> where(Object left, String condition, Object right) {
        if (queryElements.size() > 0)
            queryElements.add(QueryConjunction.AND);
        queryElements.add(new QueryCondition(left instanceof String ? new QueryColumn((String) left) : left, condition, right));
        return this;
    }

    public QueryGroup<T> where(Class<? extends Model> leftTable, String left, String operator, Class<? extends Model> rightTable, String right) {
        if (rightTable != null)
            right = Repo.get(rightTable).getInfo().getTableName() + "." + Repo.get(rightTable).getInfo().getColumnName(right);
        return where(leftTable, left, operator, new QueryColumn(right));
    }

    public QueryGroup<T> where(Class<? extends Model> leftTable, String left, String operator, Object right) {
        if (leftTable != null)
            left = Repo.get(leftTable).getInfo().getTableName() + "." + Repo.get(leftTable).getInfo().getColumnName(left);
        return where(left, operator, right);
    }

    public QueryGroup<T> whereMorph(String name, Class<? extends Model> type) {
        return where(name + "Type", Repo.get(type).getInfo().getMorphType());
    }

    public QueryGroup<T> whereMorph(String name, Class<? extends Model> type, Object id) {
        return whereMorph(name, type).where(name + "Id", id);
    }

    public QueryGroup<T> whereMorph(String name, Model entity) {
        return whereMorph(name, entity.getClass(), Repo.get(entity.getClass()).getId(entity));
    }

    public QueryGroup<T> where(Object left, Object right) {
        return where(left, "=", right);
    }

    public QueryGroup<T> isNull(Object left) {
        return where(left, "IS NULL", null);
    }

    public QueryGroup<T> notNull(Object left) {
        return where(left, "IS NOT NULL", null);
    }

    public QueryGroup<T> lessThan(Object left, Object right) {
        return where(left, "<", right);
    }

    public QueryGroup<T> greaterThan(Object left, Object right) {
        return where(left, ">", right);
    }

    public QueryGroup<T> orWhere(Object left, String condition, Object right) {
        if (queryElements.size() > 0)
            queryElements.add(QueryConjunction.OR);
        queryElements.add(new QueryCondition(left instanceof String ? new QueryColumn((String) left) : left, condition, right));
        return this;
    }

    public QueryGroup<T> orWhere(Object left, Object right) {
        return orWhere(left, "=", right);
    }

    public QueryGroup<T> orWhere(Class<? extends Model> leftTable, String left, String operator, Class<? extends Model> rightTable, String right) {
        if (rightTable != null)
            right = Repo.get(rightTable).getInfo().getTableName() + "." + Repo.get(rightTable).getInfo().getColumnName(right);
        return orWhere(leftTable, left, operator, new QueryColumn(right));
    }

    public QueryGroup<T> orWhere(Class<? extends Model> leftTable, String left, String operator, Object right) {
        if (leftTable != null)
            left = Repo.get(leftTable).getInfo().getTableName() + "." + Repo.get(leftTable).getInfo().getColumnName(left);
        return orWhere(left, operator, right);
    }

    public QueryGroup<T> orWhereMorph(String name, Class<? extends Model> type) {
        return orWhere(name + "Type", Repo.get(type).getInfo().getMorphType());
    }

    public QueryGroup<T> orWhereMorph(String name, Class<? extends Model> type, Object id) {
        return orWhereMorph(name, type).where(name + "Id", id);
    }

    public QueryGroup<T> orWhereMorph(String name, Model entity) {
        return orWhereMorph(name, entity.getClass(), Repo.get(entity.getClass()).getId(entity));
    }

    public QueryGroup<T> orIsNull(Object left) {
        return orWhere(left, "IS NULL", null);
    }

    public QueryGroup<T> orNotNull(Object left) {
        return orWhere(left, "IS NOT NULL", null);
    }

    public QueryGroup<T> orLessThan(Object left, Object right) {
        return orWhere(left, "<", right);
    }

    public QueryGroup<T> orGreaterThan(Object left, Object right) {
        return orWhere(left, ">", right);
    }

    public <M extends Model> QueryGroup<T> whereExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        if (queryElements.size() > 0)
            queryElements.add(QueryConjunction.AND);
        Query<M> query = consumer.apply(new Query<>(model).limit(1));
        queryElements.add(new QueryExists<>(query, false));
        return this;
    }

    public <M extends Model> QueryGroup<T> orWhereExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        if (queryElements.size() > 0)
            queryElements.add(QueryConjunction.OR);
        Query<M> query = consumer.apply(new Query<>(model).limit(1));
        queryElements.add(new QueryExists<>(query, false));
        return this;
    }

    public <M extends Model> QueryGroup<T> whereNotExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        if (queryElements.size() > 0)
            queryElements.add(QueryConjunction.AND);
        Query<M> query = consumer.apply(new Query<>(model).limit(1));
        queryElements.add(new QueryExists<>(query, true));
        return this;
    }

    public <M extends Model> QueryGroup<T> orWhereNotExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        if (queryElements.size() > 0)
            queryElements.add(QueryConjunction.OR);
        Query<M> query = consumer.apply(new Query<>(model).limit(1));
        queryElements.add(new QueryExists<>(query, true));
        return this;
    }

    public QueryGroup<T> whereIn(Object left, Object... values) {
        return where(left, "IN", values);
    }

    public QueryGroup<T> whereNotIn(Object left, Object... values) {
        return where(left, "NOT IN", values);
    }

    public QueryGroup<T> orWhereIn(Object left, Object... values) {
        return orWhere(left, "IN", values);
    }

    public QueryGroup<T> orWhereNotIn(Object left, Object... values) {
        return orWhere(left, "NOT IN", values);
    }

    public QueryString getQueryString(TableInfo info) {
        StringBuilder sb = new StringBuilder("(");
        List<Object> parameters = new ArrayList<>();
        for (QueryElement element : queryElements) {
            if (sb.length() > 1)
                sb.append(' ');
            QueryString s = element.getQueryString(info);
            sb.append(s.getQuery());
            parameters.addAll(s.getParameters());
        }
        sb.append(')');
        return new QueryString(sb.toString(), parameters);
    }

}
