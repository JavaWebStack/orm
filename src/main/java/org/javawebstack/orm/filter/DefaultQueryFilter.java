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
        TableInfo info = query.getRepo().getInfo();
        query.and(q -> {
            filter.keySet().stream().filter(filterable.keySet()::contains).forEach(key -> {
                boolean not = false;
                if(key.endsWith("!")) {
                    key = key.substring(0, key.length()-1);
                    not = true;
                }
                key = filterable.get(key);
                String v = filter.get(key);
                Field field = info.getField(key);
                if(field == null) {
                    key = Helper.toCamelCase(key);
                    field = info.getField(key);
                }
                if(field == null)
                    return;
                if(v.equals("null")) {
                    if(not)
                        q.whereNull(key);
                    else
                        q.whereNotNull(key);
                    return;
                }
                if(field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                    q.where(key, not ? "!=" : "=", v.equals("1") || v.equals("true"));
                    return;
                }
                q.where(key, not ? "!=" : "=", v);
            });
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
