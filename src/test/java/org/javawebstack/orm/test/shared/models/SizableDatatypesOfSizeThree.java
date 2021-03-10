package org.javawebstack.orm.test.shared.models;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

import java.util.UUID;

public class SizableDatatypesOfSizeThree extends Model {
    @Column
    int id;

    @Column(size = 3)
    short primitiveShort;

    @Column(size = 3)
    Short wrapperShort;

    @Column(size = 3)
    int primitiveInteger;

    @Column(size = 3)
    Integer wrapperInteger;

    @Column(size = 3)
    long primitiveLong;

    @Column(size = 3)
    Long wrapperLong;

    @Column(size = 3)
    String wrapperString;

    @Column(size = 3)
    char[] charArray;

    @Column(size = 3)
    byte[] byteArray;

    @Column(size = 3)
    UUID uuid;
}
