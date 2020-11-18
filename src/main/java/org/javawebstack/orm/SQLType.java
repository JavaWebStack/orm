package org.javawebstack.orm;

import java.sql.Date;
import java.sql.Timestamp;

public enum SQLType {

    VARCHAR(String.class),
    BIGINT(Long.class),
    INT(Integer.class),
    TINYINT(Boolean.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    TEXT(String.class),
    TIMESTAMP(Timestamp.class),
    DATE(Date.class),
    ENUM(String.class);

    private final Class<?> javaType;

    SQLType(Class<?> javaType){
        this.javaType = javaType;
    }

    public Class<?> getJavaType() {
        return javaType;
    }
}
