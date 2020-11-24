package org.javawebstack.orm.query;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.TableInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class QueryGroup<T extends Model> implements QueryElement {

    private final List<QueryElement> queryElements = new ArrayList<>();

    public QueryGroup(QueryElement... queryElements){
        this.queryElements.addAll(Arrays.asList(queryElements));
    }

    public List<QueryElement> getQueryElements() {
        return queryElements;
    }

    public QueryGroup<T> and(Function<QueryGroup<T>,QueryGroup<T>> group){
        QueryGroup<T> innerGroup = group.apply(new QueryGroup<>());
        if(queryElements.size() > 0)
            queryElements.add(QueryConjunction.AND);
        queryElements.add(innerGroup);
        return this;
    }

    public QueryGroup<T> or(Function<QueryGroup<T>,QueryGroup<T>> group){
        QueryGroup<T> innerGroup = group.apply(new QueryGroup<>());
        if(queryElements.size() > 0)
            queryElements.add(QueryConjunction.OR);
        queryElements.add(innerGroup);
        return this;
    }

    public QueryGroup<T> where(Object left, String condition, Object right){
        if(queryElements.size()>0)
            queryElements.add(QueryConjunction.AND);
        queryElements.add(new QueryCondition(left instanceof String ? new QueryColumn((String) left) : left, condition, right));
        return this;
    }

    public QueryGroup<T> where(Object left, Object right){
        return where(left, "=", right);
    }

    public QueryGroup<T> isNull(Object left){
        return where(left, "IS NULL", null);
    }

    public QueryGroup<T> notNull(Object left){
        return where(left, "IS NOT NULL", null);
    }

    public QueryGroup<T> lessThan(Object left, Object right){
        return where(left, "<", right);
    }

    public QueryGroup<T> greaterThan(Object left, Object right){
        return where(left, ">", right);
    }

    public QueryGroup<T> orWhere(Object left, String condition, Object right){
        if(queryElements.size() > 0)
            queryElements.add(QueryConjunction.OR);
        queryElements.add(new QueryCondition(left instanceof String ? new QueryColumn((String) left) : left, condition, right));
        return this;
    }

    public QueryGroup<T> orWhere(Object left, Object right){
        return orWhere(left, "=", right);
    }

    public QueryGroup<T> orIsNull(Object left){
        return orWhere(left, "IS NULL", null);
    }

    public QueryGroup<T> orNotNull(Object left){
        return orWhere(left, "IS NOT NULL", null);
    }

    public QueryGroup<T> orLessThan(Object left, Object right){
        return orWhere(left, "<", right);
    }

    public QueryGroup<T> orGreaterThan(Object left, Object right){
        return orWhere(left, ">", right);
    }

    public <M extends Model> QueryGroup<T> whereExists(Class<M> model, Consumer<Query<M>> consumer){
        if(queryElements.size() > 0)
            queryElements.add(QueryConjunction.AND);
        Query<M> query = new Query<>(model);
        consumer.accept(query);
        queryElements.add(new QueryExists<>(query, false));
        return this;
    }

    public <M extends Model> QueryGroup<T> orWhereExists(Class<M> model, Consumer<Query<M>> consumer){
        if(queryElements.size() > 0)
            queryElements.add(QueryConjunction.OR);
        Query<M> query = new Query<>(model);
        consumer.accept(query);
        queryElements.add(new QueryExists<>(query, false));
        return this;
    }

    public <M extends Model> QueryGroup<T> whereNotExists(Class<M> model, Consumer<Query<M>> consumer){
        if(queryElements.size() > 0)
            queryElements.add(QueryConjunction.AND);
        Query<M> query = new Query<>(model);
        consumer.accept(query);
        queryElements.add(new QueryExists<>(query, true));
        return this;
    }

    public <M extends Model> QueryGroup<T> orWhereNotExists(Class<M> model, Consumer<Query<M>> consumer){
        if(queryElements.size() > 0)
            queryElements.add(QueryConjunction.OR);
        Query<M> query = new Query<>(model);
        consumer.accept(query);
        queryElements.add(new QueryExists<>(query, true));
        return this;
    }

    public QueryString getQueryString(TableInfo info){
        StringBuilder sb = new StringBuilder("(");
        List<Object> parameters = new ArrayList<>();
        for(QueryElement element : queryElements){
            if(sb.length() > 1)
                sb.append(' ');
            QueryString s = element.getQueryString(info);
            sb.append(s.getQuery());
            parameters.addAll(s.getParameters());
        }
        sb.append(')');
        return new QueryString(sb.toString(), parameters);
    }

}
