package org.javawebstack.orm.filter;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.query.Query;

import java.util.List;
import java.util.Map;

public class DefaultQueryFilter implements QueryFilter {

    private final Map<String, String> filterable;
    private final List<String> searchable;

    public DefaultQueryFilter(Map<String, String> filterable, List<String> searchable){
        this.filterable = filterable;
        this.searchable = searchable;
    }

    public void filter(Query<? extends Model> query, Map<String, String> filter) {
        query.and(q -> {
            filter.keySet().stream().filter(filterable.keySet()::contains).forEach(key -> q.where(filterable.get(key), filter.get(key)));
            return q;
        });
    }

    public void search(Query<? extends Model> query, String search) {
        query.and(q -> {
            searchable.forEach(key -> q.orWhere(key, "LIKE", "%"+search+"%"));
            return q;
        });
    }

}
