package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class QueryCondition implements QueryElement {

    private final Object left;
    private final String operator;
    private final Object right;

    public QueryCondition(Object left, String operator, Object right){
        this.left = left;
        this.operator = operator; // TODO Validate and throw exception
        this.right = right;
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
        List<Object> parameters = new ArrayList<>();
        if(left instanceof QueryColumn){
            String fieldName = ((QueryColumn) left).getName();
            sb.append('`').append(info.getColumnName(fieldName)).append('`');
        }else{
            sb.append('?');
            parameters.add(left);
        }
        sb.append(' ');
        sb.append(operator);
        if(hasRight()){
            sb.append(' ');
            if(right instanceof QueryColumn){
                String fieldName = ((QueryColumn) right).getName();
                sb.append('`').append(info.getColumnName(fieldName)).append('`');
            }else{
                sb.append('?');
                parameters.add(right);
            }
        }
        return new QueryString(sb.toString(), parameters);
    }

}
