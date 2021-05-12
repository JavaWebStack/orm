package org.javawebstack.orm.test.shared.models.columnnames;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

public class OverwrittenColumnName extends Model {
    @Column
    int id;

    @Column(name = "oVer_writtenColumn-name")
    String dummyString;
}
