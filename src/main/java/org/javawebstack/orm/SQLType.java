package org.javawebstack.orm;

import java.sql.Date;
import java.sql.Timestamp;

public enum SQLType {

    // Divided by category and ordered by byte size (MySQL sizes)
    TINYINT(Boolean.class),
    SMALLINT(Short.class),
    // Not in use for any java data type
    //MEDIUMINT(),
    INT(Integer.class),
    BIGINT(Long.class),

    FLOAT(Float.class),
    DOUBLE(Double.class),

    VARCHAR(String.class),
    TEXT(String.class),

    DATE(Date.class),
    TIMESTAMP(Timestamp.class),

    ENUM(String.class),

    VARBINARY(byte[].class);

    private final Class<?> javaType;

    SQLType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public Class<?> getJavaType() {
        return javaType;
    }
}
