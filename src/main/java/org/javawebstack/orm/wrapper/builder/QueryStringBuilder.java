package org.javawebstack.orm.wrapper.builder;

import org.javawebstack.orm.TableInfo;
import org.javawebstack.orm.query.*;

import java.util.Map;

public interface QueryStringBuilder {

    SQLQueryString buildInsert(TableInfo info, Map<String, Object> values);
    SQLQueryString buildQuery(Query<?> query);
    SQLQueryString buildUpdate(Query<?> query, Map<String, Object> values);
    SQLQueryString buildDelete(Query<?> query);

}
