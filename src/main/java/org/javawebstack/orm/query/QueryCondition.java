package org.javawebstack.orm.query;

import org.javawebstack.orm.exception.ORMQueryException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class QueryCondition implements QueryElement {

    private static final List<String> VALID_OPERATORS = Arrays.asList(
            "=",
            "<=>",
            "!=",
            "<>",
            "<=",
            ">=",
            "<",
            ">",
            "is null",
            "is not null",
            "is",
            "is not",
            "in",
            "not in",
            "like",
            "not like"
    );

    private final Object left;
    private final String operator;
    private final Object right;
    private final boolean not;

    public QueryCondition(Object left, String operator, Object right, boolean not) {
        validateOperator(operator);
        this.left = left;
        this.operator = operator;
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

    private static void validateOperator(String operator) {
        if(!VALID_OPERATORS.contains(operator.toLowerCase(Locale.ROOT)))
            throw new ORMQueryException("The given operator '" + operator + "' is invalid or not supported");
    }

    public enum Operator {
        
    }

}
