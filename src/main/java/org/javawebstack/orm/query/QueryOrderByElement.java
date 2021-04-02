package org.javawebstack.orm.query;

import java.util.Objects;

public class QueryOrderByElement {
    private QueryColumn queryColumn;
    private boolean desc;

    QueryOrderByElement(String columnName, boolean desc) {
        queryColumn = new QueryColumn(columnName);
        this.desc = desc;
    }

    QueryOrderByElement(QueryColumn column, boolean desc) {
        this.queryColumn = column;
        this.desc = desc;
    }

    public QueryColumn getQueryColumn() {
        return queryColumn;
    }

    public boolean isDesc() {
        return desc;
    }

    public boolean hasEqualColumn(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryOrderByElement that = (QueryOrderByElement) o;
        return getQueryColumn().equals(that.getQueryColumn());
    }

    public boolean hasEqualOrderDirection(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryOrderByElement that = (QueryOrderByElement) o;
        return isDesc() == that.isDesc();
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
}
