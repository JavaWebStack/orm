package org.javawebstack.orm.test.shared.models.tablenames;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;

/**
 * Class with the intention to be one name in singular. Short for "One Word" to fit into one word.
 */
public class Word extends Model {

    @Column
    int id;
}
