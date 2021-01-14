package org.javawebstack.orm.filter;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.query.Query;

import java.util.Map;

public interface QueryFilter {

    void filter(Query<? extends Model> query, Map<String, String> filter);
    void search(Query<? extends Model> query, String search);

}
