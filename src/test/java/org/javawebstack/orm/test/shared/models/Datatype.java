package org.javawebstack.orm.test.shared.models;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
 * Collection of all considered datatypes for columns.
 */
public class Datatype extends Model {
    @Column
    int id;

    @Column
    boolean primitiveBoolean;

    @Column
    Boolean wrapperBoolean;

    @Column
    byte primitiveByte;

    @Column
    Byte wrapperByte;

    @Column
    short primitiveShort;

    @Column
    Short wrapperShort;

    @Column
    int primitiveInteger;

    @Column
    Integer wrapperInteger;

    @Column
    long primitiveLong;

    @Column
    Long wrapperLong;

    @Column
    float primitiveFloat;

    @Column
    Float wrapperFloat;

    @Column
    double primitiveDouble;

    @Column
    Double wrapperDouble;

    @Column
    char primitiveChar;

    @Column
    String wrapperString;

    @Column
    char[] charArray;

    @Column
    byte[] byteArray;

    @Column
    Timestamp timestamp;

    @Column
    Date date;

    @Column
    UUID uuid;

    @Column
    OptionEnum optionEnum;

    public enum OptionEnum {
        OPTION1,
        OPTION2,
    }

    public static final List<String> columnNames = Arrays.asList(
            "id",
            "primitive_boolean",
            "wrapper_boolean",
            "primitive_byte",
            "wrapper_byte",
            "primitive_short",
            "wrapper_short",
            "primitive_integer",
            "wrapper_integer",
            "primitive_long",
            "wrapper_long",
            "primitive_float",
            "wrapper_float",
            "primitive_double",
            "wrapper_double",
            "primitive_char",
            "wrapper_string",
            "char_array",
            "byte_array",
            "timestamp",
            "date",
            "uuid",
            "option_enum"
    );
}