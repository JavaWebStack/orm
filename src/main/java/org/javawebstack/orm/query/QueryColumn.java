package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

import java.util.Arrays;
import java.util.stream.Collectors;

public class QueryColumn {

    private final String name;
    private final boolean raw;

    public QueryColumn(String name){
        this(name, false);
    }
    public QueryColumn(String name, boolean raw){
        this.name = name;
        this.raw = raw;
    }

    public String getName() {
        return name;
    }

    public boolean isRaw() {
        return raw;
    }

    public String toString(){
        return toString(null);
    }

    public String toString(TableInfo info){
        if(raw)
            return name;
        return Arrays.stream((info != null ? info.getColumnName(name) : name).split("\\.")).map(s -> "`"+s+"`").collect(Collectors.joining("."));
    }

}
