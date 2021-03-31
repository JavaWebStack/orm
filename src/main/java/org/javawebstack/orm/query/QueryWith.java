package org.javawebstack.orm.query;

public class QueryWith {

    private final String expression;
    private final String as;

    public QueryWith(String expression, String as) {
        this.expression = expression;
        this.as = as;
    }

    public String getExpression() {
        return expression;
    }

    public String getAs() {
        return as;
    }
}
