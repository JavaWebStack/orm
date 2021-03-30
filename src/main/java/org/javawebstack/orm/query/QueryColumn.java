package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;
import org.javawebstack.orm.exception.ORMQueryException;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryColumn {

    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private final String name;
    private final boolean raw;

    public QueryColumn(String name) {
        this(name, false);
    }

    public QueryColumn(String name, boolean raw) {
        if(!raw)
            validateName(name);
        this.name = name;
        this.raw = raw;
    }

    public String getName() {
        return name;
    }

    public boolean isRaw() {
        return raw;
    }

    public String toString() {
        return toString(null);
    }

    public String toString(TableInfo info) {
        if (raw)
            return name;
        return Arrays.stream((info != null ? info.getColumnName(name) : name).split("\\.")).map(s -> "`" + s + "`").collect(Collectors.joining("."));
    }

    private static void validateName(String name) {
        if(!NAME_PATTERN.matcher(name).matches())
            throw new ORMQueryException("Invalid column name '" + name + "' (Use raw in case you know what you're doing)");
    }

}
