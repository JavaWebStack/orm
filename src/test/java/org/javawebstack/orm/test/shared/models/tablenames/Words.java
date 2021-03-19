package org.javawebstack.orm.test.shared.models.tablenames;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

/**
 * Like {@link Word} but in plural
 */
public class Words extends Model {
    @Column
    int id;
}
