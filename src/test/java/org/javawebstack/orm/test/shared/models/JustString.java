package org.javawebstack.orm.test.shared.models;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

public class JustString extends Model {
    @Column
    int id;

    @Column
    String string;

}
