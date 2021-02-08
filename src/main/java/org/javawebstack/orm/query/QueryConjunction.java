package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

public enum QueryConjunction implements QueryElement {
    AND,
    OR,
    XOR;

    public QueryString getQueryString(TableInfo info) {
        return new QueryString(name());
    }
}
