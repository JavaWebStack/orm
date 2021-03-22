package org.javawebstack.orm.test.shared.models.tablenames;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

/**
 * This class' name has an irregular plural (mice) and is here for this purpose.
 */
public class Mouse extends Model {
    @Column
    int id;
}
