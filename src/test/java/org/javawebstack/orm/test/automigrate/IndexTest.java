package org.javawebstack.orm.test.automigrate;

import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.IndexType;
import org.javawebstack.orm.test.shared.verification.Indexes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class IndexTest extends ORMTestCase {

    @BeforeEach
    public void setUp() throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(IndexType.class, sql(), config);
        ORM.autoMigrate(true);
    }

    @Test
    public void hasIndexes() throws SQLException {
        Indexes indexes = new Indexes("index_types");
        indexes.assertHasIndex("idx_key_value"); // auto generated
        indexes.assertHasIndex("idx_custom"); // explicit set
    }

    @Test
    public void testUnique() throws SQLException {
        Indexes indexes = new Indexes("index_types");
        indexes.assertIsUnique("idx_key");
    }
}
