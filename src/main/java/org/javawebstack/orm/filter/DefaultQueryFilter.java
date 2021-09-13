package org.javawebstack.orm.filter;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.TableInfo;
import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.util.Helper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class DefaultQueryFilter implements QueryFilter {

    private final Map<String, String> filterable;
    private final List<String> searchable;

    public DefaultQueryFilter(Map<String, String> filterable, List<String> searchable) {
        this.filterable = filterable;
        this.searchable = searchable;
    }

    public void filter(Query<? extends Model> query, Map<String, String> filter) {
        if(filter.size() == 0)
            return;
        TableInfo info = query.getRepo().getInfo();
        query.and(q -> {
            filter.forEach((key, v) -> {
                if(!filterable.containsKey(key))
                    key = Helper.toCamelCase(key);
                if(!filterable.containsKey(key)) {
                    System.out.println("Filterable not found: " + key);
                    return;
                }
                Field field = info.getField(filterable.get(key));
                if(field == null) {
                    System.out.println("Field null!");
                    return;
                }
                if(v.equals("null")) {
                    q.whereNull(key);
                    return;
                }
                if(field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                    q.where(key, "=", v.equals("1") || v.equals("true"));
                    return;
                }
                q.where(key, "=", v);
            });
            if(q.getQueryElements().size() == 0)
                q.where(1, "=", 1);
            return q;
        });
    }

    public void search(Query<? extends Model> query, String search) {
        query.and(q -> {
            searchable.forEach(key -> q.orWhere(key, "LIKE", "%" + search + "%"));
            return q;
        });
    }

}
