package org.javawebstack.orm.query;

import org.javawebstack.orm.Model;

public class QueryExists<T extends Model> implements QueryElement {

    private final Query<T> query;
    private final boolean not;

    public QueryExists(Query<T> query, boolean not) {
        this.query = query;
        this.not = not;
    }

    public Query<T> getQuery() {
        return query;
    }

    public boolean isNot() {
        return not;
    }

}
