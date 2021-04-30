package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * The QueryOrderBy class serves as an aggregation of order by elements. It extends a list, because the order of the
 * order by statements is of relevance.
 */
public class QueryOrderBy extends LinkedList<QueryOrderByElement>{

    /**
     * Add a new order by statement. If a statement with the same column name already exists it will not add the
     * statement.
     *
     * @param columnName The column name to order by.
     * @param desc If the column should be order descendingly.
     * @return True if adding the statement was successful. False otherwise.
     */
    public boolean add(String columnName, boolean desc) {
        return this.add(new QueryColumn(columnName), desc);
    }

    /**
     * Add a new order by statement. If a statement with the same column name already exists it will not add the
     * statement.
     *
     * @param column The column to be ordered by. It will retrieve the name from the QueryColumn.
     * @param desc If the column should be order descendingly.
     * @return True if adding the statement was successful. False otherwise.
     */
    public boolean add(QueryColumn column, boolean desc) {
        return this.add(new QueryOrderByElement(column, desc));
    }

    @Override
    /**
     * Add a new order by statement. If a statement with the same column name already exists it will not add the
     * statement.
     *
     * @param element The direct QueryOrderByElement which encodes the order by statement.
     * @return True if adding the statement was successful. False otherwise.
     */
    public boolean add(QueryOrderByElement element) {
        boolean hasBeenAdded = false;
        if(!willOverwrite(element))
            hasBeenAdded = super.add(element);

        return hasBeenAdded;
    }

    private boolean willOverwrite(QueryOrderByElement element) {
        return this.stream().anyMatch(element::hasEqualColumn);
    }


    // The toString methods are specific to MySQL so they might have to be replaced later on.
    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(TableInfo info) {
        return this.stream()
                .map(singleOrderByElement -> singleOrderByElement.toString(info))
                .collect(Collectors.joining(","));
    }
}
