package org.javawebstack.orm.query;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Queries grouped via the QueryGroup class will be put inside parenthesis.
 * This makes expressions as the following possible (MySQL example):
 * ... `column_a` = 'A' OR (`column_b` = 'B' AND `column_c´ = 'C') ...
 *
 * In the above example `column_b` = 'B' AND `column_c´ = 'C' would be in a QueryGroup.
 *
 * @param <T> The model under which the QueryGroups functions. Currently purely semantic without functionality.
 */
public class QueryGroup<T extends Model> implements QueryElement {

    private final List<QueryElement> queryElements = new ArrayList<>();
    private boolean immutable;

    public QueryGroup(QueryElement... queryElements) {
        this(false, queryElements);
    }

    public QueryGroup(boolean immutable, QueryElement... queryElements) {
        this.queryElements.addAll(Arrays.asList(queryElements));
        this.immutable = immutable;
    }

    public QueryGroup<T> clone() {
        return new QueryGroup(immutable, queryElements.toArray(new QueryElement[0]));
    }

    public QueryGroup<T> mutable() {
        if(!this.immutable)
            return this;
        QueryGroup<T> cloned = clone();
        cloned.immutable = false;
        return cloned;
    }

    public QueryGroup<T> immutable() {
        if(this.immutable)
            return this;
        QueryGroup<T> cloned = clone();
        cloned.immutable = true;
        return cloned;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public List<QueryElement> getQueryElements() {
        return queryElements;
    }

    @Deprecated
    public QueryGroup<T> and(Function<QueryGroup<T>, QueryGroup<T>> group) {
        return where(group);
    }

    public QueryGroup<T> where(Function<QueryGroup<T>, QueryGroup<T>> group) {
        QueryGroup<T> q = immutable ? clone() : this;
        QueryGroup<T> innerGroup = group.apply(new QueryGroup<>());
        if(innerGroup.queryElements.isEmpty())
            return q;
        if (!q.queryElements.isEmpty())
            q.queryElements.add(QueryConjunction.AND);
        q.queryElements.add(innerGroup);
        return q;
    }

    @Deprecated
    public QueryGroup<T> or(Function<QueryGroup<T>, QueryGroup<T>> group) {
        return orWhere(group);
    }

    public QueryGroup<T> orWhere(Function<QueryGroup<T>, QueryGroup<T>> group) {
        QueryGroup<T> q = immutable ? clone() : this;
        QueryGroup<T> innerGroup = group.apply(new QueryGroup<>());
        if(innerGroup.queryElements.isEmpty())
            return q;
        if (!q.queryElements.isEmpty())
            q.queryElements.add(QueryConjunction.OR);
        q.queryElements.add(innerGroup);
        return q;
    }

    public QueryGroup<T> where(Object left, String condition, Object right) {
        if(condition.equalsIgnoreCase("=") && right == null)
            return whereNull(left);
        if(condition.equalsIgnoreCase("!=") && right == null)
            return whereNotNull(left);

        if(condition.equalsIgnoreCase("IN") || condition.equalsIgnoreCase("NOT IN")) {
            Object[] values = (Object[]) right;
            if (values != null && values.length == 1) {
                if (values[0] instanceof Collection)
                    values = ((Collection<?>) values[0]).toArray();
                else if (values[0] instanceof Stream)
                    values = ((Stream<?>) values[0]).toArray();
            }
            if(values == null || values.length == 0) {
                left = 1;
                condition = "=";
                right = 2;
            }
        }
        QueryGroup<T> q = immutable ? clone() : this;
        if (!q.queryElements.isEmpty())
            q.queryElements.add(QueryConjunction.AND);
        q.queryElements.add(new QueryCondition(left instanceof String ? new QueryColumn((String) left) : left, condition, right));
        return q;
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

    public QueryGroup<T> where(Object left, Object right) {
        return where(left, "=", right);
    }

    @Deprecated
    public QueryGroup<T> isNull(Object left) {
        return whereNull(left);
    }

    @Deprecated
    public QueryGroup<T> notNull(Object left) {
        return whereNotNull(left);
    }
    
    public QueryGroup<T> whereNull(Object left) {
        return where(left, "IS NULL", null);
    }
    
    public QueryGroup<T> whereNotNull(Object left) {
        return where(left, "IS NOT NULL", null);
    }

    public QueryGroup<T> orWhere(Object left, String condition, Object right) {
        QueryGroup<T> q = immutable ? clone() : this;
        if(condition.equalsIgnoreCase("=") && right == null)
            return orIsNull(left);
        if(condition.equalsIgnoreCase("!=") && right == null)
            return orNotNull(left);
        if((condition.equalsIgnoreCase("IN") || condition.equalsIgnoreCase("NOT IN")) && (right == null || Array.getLength(right) == 0)) {
            left = 1;
            condition = "=";
            right = 2;
        }
        if (!q.queryElements.isEmpty())
            q.queryElements.add(QueryConjunction.OR);
        q.queryElements.add(new QueryCondition(left instanceof String ? new QueryColumn((String) left) : left, condition, right));
        return q;
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

    @Deprecated
    public QueryGroup<T> orIsNull(Object left) {
        return orWhereNull(left);
    }

    @Deprecated
    public QueryGroup<T> orNotNull(Object left) {
        return orWhereNotNull(left);
    }

    public QueryGroup<T> orWhereNull(Object left) {
        return orWhere(left, "IS NULL", null);
    }

    public QueryGroup<T> orWhereNotNull(Object left) {
        return orWhere(left, "IS NOT NULL", null);
    }

    public <M extends Model> QueryGroup<T> whereExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        QueryGroup<T> q = immutable ? clone() : this;
        if (!q.queryElements.isEmpty())
            q.queryElements.add(QueryConjunction.AND);
        Query<M> query = consumer.apply(new Query<>(model, immutable).limit(1));
        q.queryElements.add(new QueryExists<>(query, false));
        return q;
    }

    public <M extends Model> QueryGroup<T> orWhereExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        QueryGroup<T> q = immutable ? clone() : this;
        if (!q.queryElements.isEmpty())
            q.queryElements.add(QueryConjunction.OR);
        Query<M> query = consumer.apply(new Query<>(model, immutable).limit(1));
        q.queryElements.add(new QueryExists<>(query, false));
        return q;
    }

    public <M extends Model> QueryGroup<T> whereNotExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        QueryGroup<T> q = immutable ? clone() : this;
        if (!q.queryElements.isEmpty())
            q.queryElements.add(QueryConjunction.AND);
        Query<M> query = consumer.apply(new Query<>(model, immutable).limit(1));
        q.queryElements.add(new QueryExists<>(query, true));
        return q;
    }

    public <M extends Model> QueryGroup<T> orWhereNotExists(Class<M> model, Function<Query<M>, Query<M>> consumer) {
        QueryGroup<T> q = immutable ? clone() : this;
        if (!q.queryElements.isEmpty())
            q.queryElements.add(QueryConjunction.OR);
        Query<M> query = consumer.apply(new Query<>(model, immutable).limit(1));
        q.queryElements.add(new QueryExists<>(query, true));
        return q;
    }

    public QueryGroup<T> has(Query<?> relation, String operator, int count) {
        return where(relation.select("count(*)"), operator, count);
    }

    public QueryGroup<T> has(Query<?> relation) {
        return has(relation, ">=", 1);
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

}
