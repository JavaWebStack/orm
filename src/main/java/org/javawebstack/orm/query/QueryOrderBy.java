package org.javawebstack.orm.query;

import java.util.LinkedList;
import java.util.List;

public class QueryOrderBy extends LinkedList<QueryOrderByElement>{

    public boolean add(String columnName, boolean desc) {
        return this.add(new QueryColumn(columnName), desc);
    }

    public boolean add(QueryColumn column, boolean desc) {
        return this.add(new QueryOrderByElement(column, desc));
    }

    @Override
    public boolean add(QueryOrderByElement element) {
        boolean hasBeenAdded = false;
        if(!willOverwrite(element))
            hasBeenAdded = super.add(element);

        return hasBeenAdded;
    }

    private boolean willOverwrite(QueryOrderByElement element) {
        return this.stream().anyMatch(element::hasEqualColumn);
    }
}
