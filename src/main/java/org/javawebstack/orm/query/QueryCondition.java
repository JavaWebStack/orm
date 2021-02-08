package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QueryCondition implements QueryElement {

    private final Object left;
    private final String operator;
    private final Object right;
    private final boolean not;

    public QueryCondition(Object left, String operator, Object right, boolean not){
        this.left = left;
        this.operator = operator; // TODO Validate and throw exception
        this.right = right;
        this.not = not;
    }

    public QueryCondition(Object left, String operator, Object right){
        this(left, operator, right, false);
    }

    public Object getLeft(){
        return left;
    }

    public Object getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    private boolean hasRight(){
        return !(operator.equalsIgnoreCase("IS NULL") || operator.equalsIgnoreCase("IS NOT NULL"));
    }

    public QueryString getQueryString(TableInfo info){
        StringBuilder sb = new StringBuilder();
        if(not)
            sb.append("NOT ");
        List<Object> parameters = new ArrayList<>();
        if(left instanceof QueryColumn){
            sb.append(((QueryColumn) left).toString(info));
        }else{
            sb.append('?');
            parameters.add(left);
        }
        sb.append(' ');
        sb.append(operator);
        if(hasRight()){
            sb.append(' ');
            if(operator.endsWith("IN")) {
                Object[] values = (Object[]) right;
                sb.append("(").append(IntStream.range(0, values.length).mapToObj(i -> "?").collect(Collectors.joining(","))).append(")");
                parameters.addAll(Arrays.asList(values));
            } else if(right instanceof QueryColumn) {
                sb.append(((QueryColumn) right).toString(info));
            } else {
                sb.append('?');
                parameters.add(right);
            }
        }
        return new QueryString(sb.toString(), parameters);
    }

}
