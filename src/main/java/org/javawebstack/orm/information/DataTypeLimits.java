package org.javawebstack.orm.information;

public class DataTypeLimits {
    // SOURCES:
    // TEXT Datatypes: https://www.mysqltutorial.org/mysql-text/
    public static final int BYTES_OVERHEAD_VARCHAR = 4;
    public static final int BYTES_OVERHEAD_TINYTEXT = 1;
    public static final int BYTES_OVERHEAD_TEXT = 2;
    public static final int BYTES_OVERHEAD_MEDIUMTEXT = 3;
    public static final int BYTES_OVERHEAD_LONGTEXT = 4;

    // The max sizes given in the manual are in bytes. There are overheads which need to be subtracted.
    // The following values assume utf8mb4 encoding which uses 4 bytes per character and
    // further quarters the maximum column length accordingly.
    public static final long MAX_SIZE_VARCHAR = (long) Math.floor((65535 - BYTES_OVERHEAD_VARCHAR) / 4);
    public static final long MAX_SIZE_MEDIUMTEXT = (long) Math.floor((16777215 - BYTES_OVERHEAD_MEDIUMTEXT) / 4);
    public static final long MAX_SIZE_LONGTEXT = (long) Math.floor((4294967295L - BYTES_OVERHEAD_LONGTEXT) / 4);
}
