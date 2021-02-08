package org.javawebstack.orm.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryString {

    private final String query;
    private final List<Object> parameters;

    public QueryString(String query, List<Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public QueryString(String query, Object... parameters) {
        this(query, new ArrayList<>(Arrays.asList(parameters)));
    }

    public QueryString(String query) {
        this(query, new ArrayList<>());
    }

    public String getQuery() {
        return query;
    }

    public List<Object> getParameters() {
        return parameters;
    }
}
