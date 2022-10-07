package org.javawebstack.orm;

import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.query.QueryGroup;

public interface Accessible {
    <T extends Model> QueryGroup<T> access(Query<T> query, QueryGroup<T> accessChecks, Object accessor);
}
