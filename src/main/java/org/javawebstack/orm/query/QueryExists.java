package org.javawebstack.orm.query;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.TableInfo;

public class QueryExists<T extends Model> implements QueryElement {

    private final Query<T> query;
    private final boolean not;

    public QueryExists(Query<T> query, boolean not) {
        this.query = query;
        this.not = not;
    }

    public QueryString getQueryString(TableInfo info) {
        QueryString qs = query.getQueryString();
        return new QueryString((not ? "NOT " : "") + "EXISTS (" + qs.getQuery() + ")", qs.getParameters());
    }

}
