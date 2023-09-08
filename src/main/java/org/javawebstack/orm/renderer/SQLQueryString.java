package org.javawebstack.orm.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLQueryString {

    private final String query;
    private final List<Object> parameters;

    public SQLQueryString(String query, List<Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public SQLQueryString(String query, Object... parameters) {
        this(query, new ArrayList<>(Arrays.asList(parameters)));
    }

    public SQLQueryString(String query) {
        this(query, new ArrayList<>());
    }

    public String getQuery() {
        return query;
    }

    public List<Object> getParameters() {
        return parameters;
    }
}
