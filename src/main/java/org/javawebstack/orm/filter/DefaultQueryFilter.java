package org.javawebstack.orm.filter;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.query.Query;

import java.util.List;
import java.util.Map;

public class DefaultQueryFilter implements QueryFilter {

    private final List<String> filterable;
    private final List<String> searchable;

    public DefaultQueryFilter(List<String> filterable, List<String> searchable){
        this.filterable = filterable;
        this.searchable = searchable;
    }

    public void filter(Query<? extends Model> query, Map<String, String> filter) {
        query.and(q -> {
            filter.keySet().stream().filter(filterable::contains).forEach(key -> q.where(key, filter.get(key)));
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
