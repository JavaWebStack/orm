package org.javawebstack.orm.renderer;

import org.javawebstack.orm.TableInfo;
import org.javawebstack.orm.query.Query;

import java.util.Map;

public interface QueryStringRenderer {

    SQLQueryString buildInsert(TableInfo info, Map<String, Object> values);
    SQLQueryString buildQuery(Query<?> query);
    SQLQueryString buildUpdate(Query<?> query, Map<String, Object> values);
    SQLQueryString buildDelete(Query<?> query);

}
