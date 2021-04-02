package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

import java.util.Objects;

/**
 * The QueryOrderByElement class encodes an Order By Statement.
 */
public class QueryOrderByElement {
    private final QueryColumn queryColumn;
    private final boolean desc;

    QueryOrderByElement(String columnName, boolean desc) {
        queryColumn = new QueryColumn(columnName);
        this.desc = desc;
    }

    QueryOrderByElement(QueryColumn column, boolean desc) {
        this.queryColumn = column;
        this.desc = desc;
    }

    /**
     * Retrieves the QueryColumn of the statement which encodes the column name.
     *
     * @return The encoding QueryColumn object.
     */
    public QueryColumn getQueryColumn() {
        return queryColumn;
    }

    /**
     * Retrieves the information if this column is ordered ascendingly or descendingly.
     *
     * @return false if ascending, true if descending.
     */
    public boolean isDesc() {
        return desc;
    }

    /**
     * Compares the encoded column name.
     *
     * @param o An object to compare to.
     * @return True if the object is a QueryOrderByElement with a QueryColumn with generates the same identifier.
     */
    public boolean hasEqualColumn(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryOrderByElement that = (QueryOrderByElement) o;
        return getQueryColumn().equals(that.getQueryColumn());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryOrderByElement that = (QueryOrderByElement) o;
        return isDesc() == that.isDesc() && getQueryColumn().equals(that.getQueryColumn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQueryColumn(), isDesc());
    }

    @Override
    public String toString() {
        return this.toString(null);
    }

    public String toString(TableInfo info) {
        String stringifiedOrderBy = getQueryColumn().toString(info);
        if (isDesc())
            stringifiedOrderBy += " DESC";

        return stringifiedOrderBy;
    }
}
