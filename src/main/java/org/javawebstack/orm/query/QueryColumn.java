package org.javawebstack.orm.query;

import java.util.Arrays;
import java.util.stream.Collectors;

public class QueryColumn {

    private final String name;

    public QueryColumn(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return Arrays.stream(name.split("\\.")).map(s -> "`"+s+"`").collect(Collectors.joining("."));
    }

}
