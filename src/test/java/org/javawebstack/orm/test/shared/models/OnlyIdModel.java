package org.javawebstack.orm.test.shared.models;

import lombok.Getter;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

@Getter
public class OnlyIdModel extends Model {

    @Column
    int id;

}
