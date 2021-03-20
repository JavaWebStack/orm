package org.javawebstack.orm.test.shared.models.tablenames;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

/**
 * As the class {@link Word}
 */
public class Words extends Model {
    @Column
    int id;
}
