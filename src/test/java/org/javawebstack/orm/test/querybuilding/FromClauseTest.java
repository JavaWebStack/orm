package org.javawebstack.orm.test.querybuilding;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.tablenames.One;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The focus does not lie on a correct plural with this test, therefore we are only testing it sample-wise.
 * For tests see the library that is used in TableInfo: https://github.com/atteo/evo-inflector
 */
class FromClauseTest extends ORMTestCase {

    @Test
    void testOneWordSAppendixPlural() throws ORMConfigurationException {
        ORM.register(Word.class, sql());
        String query = getBaseQuery(Word.class);
        assertTrue(query.contains("FROM `ones`"));
    }
}
