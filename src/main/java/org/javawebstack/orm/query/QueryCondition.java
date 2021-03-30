package org.javawebstack.orm.query;

public class QueryCondition implements QueryElement {

    private final Object left;
    private final String operator;
    private final Object right;
    private final boolean not;

    public QueryCondition(Object left, String operator, Object right, boolean not) {
        this.left = left;
        this.operator = operator; // TODO Validate and throw exception
        this.right = right;
        this.not = not;
    }

    public QueryCondition(Object left, String operator, Object right) {
        this(left, operator, right, false);
    }

    public Object getLeft() {
        return left;
    }

    public Object getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    public boolean hasRight() {
        return !(operator.equalsIgnoreCase("IS NULL") || operator.equalsIgnoreCase("IS NOT NULL"));
    }

    public boolean isNot() {
        return not;
    }

}
