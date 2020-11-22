package org.javawebstack.orm;

import org.javawebstack.orm.query.Query;

public interface Accessible<T extends Model> {
    Query<T> access(Query<T> query, Object accessor);
}
