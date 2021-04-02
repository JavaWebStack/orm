package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

import java.util.LinkedList;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(TableInfo info) {
        return this.stream()
                .map(QueryOrderByElement::toString)
                .collect(Collectors.joining(","));
    }
}
