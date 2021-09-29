package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

import java.util.Objects;

/**
 * The QueryOrderBy class encodes an Order By Statement.
 */
public class QueryOrderBy {

    private final QueryColumn queryColumn;
    private final boolean desc;

    QueryOrderBy(String columnName, boolean desc) {
        queryColumn = new QueryColumn(columnName);
        this.desc = desc;
    }

    QueryOrderBy(QueryColumn column, boolean desc) {
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
     * @return True if the object is a QueryOrderBy with a QueryColumn with generates the same identifier.
     */
    public boolean hasEqualColumn(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryOrderBy that = (QueryOrderBy) o;
        return getQueryColumn().equals(that.getQueryColumn());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryOrderBy that = (QueryOrderBy) o;
        return isDesc() == that.isDesc() && getQueryColumn().equals(that.getQueryColumn());
    }

    public int hashCode() {
        return Objects.hash(getQueryColumn(), isDesc());
    }

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
