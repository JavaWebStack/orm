package org.javawebstack.orm;

import java.sql.Date;
import java.sql.Timestamp;

// The class passed as constructor determines
public enum SQLType {

    // Divided by category and ordered by byte size (MySQL sizes)
    TINYINT(Boolean.class),
    SMALLINT(Short.class),
    MEDIUMINT(Integer.class),
    INT(Integer.class),
    BIGINT(Long.class),

    FLOAT(Float.class),
    DOUBLE(Double.class),
    DECIMAL(Double.class),

    // No native char method except for a char stream
    CHAR(String.class),

    VARCHAR(String.class),
    TINYTEXT(String.class),
    TEXT(String.class),
    MEDIUMTEXT(String.class),
    LONGTEXT(String.class),

    DATE(Date.class),
    TIMESTAMP(Timestamp.class),
    DATETIME(Timestamp.class),

    ENUM(String.class),

    VARBINARY(byte[].class),
    TINYBLOB(byte[].class),
    BLOB(byte[].class),
    MEDIUMBLOB(byte[].class),
    LONGBLOB(byte[].class);

    private final Class<?> javaType;

    SQLType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public Class<?> getJavaType() {
        return javaType;
    }
}
