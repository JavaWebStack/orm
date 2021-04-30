package org.javawebstack.orm.test.shared.models.tablenames;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Table;

/**
 * This class overwrites the model name to a seemingly random word to test multiple cases at once.
 */
@Table("oVer_writtenValue")
public class OverwrittenTableName extends Model {
    @Column
    int id;
}
