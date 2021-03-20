package org.javawebstack.orm.test.querybuilding;

import org.atteo.evo.inflector.English;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.tablenames.TwoWord;
import org.javawebstack.orm.test.shared.models.tablenames.Word;
import org.javawebstack.orm.test.shared.models.tablenames.Words;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertTrue(query.contains("FROM `words`"));
    }

    @Test
    void testTwoWordsBecomeSnakeCases() throws ORMConfigurationException {
        ORM.register(TwoWord.class, sql());
        String query = getBaseQuery(TwoWord.class);
        assertTrue(query.contains("FROM `two_words`"));
    }

    /*
     * Error / Not Closer Specified Cases
     */
    @Test
    void testOneWordAlreadyInPluralDoesntWork() throws ORMConfigurationException {
        ORM.register(Words.class, sql());
        String query = getBaseQuery(Words.class);
        // Should try to find a non-sense plural and not map to itself as plural; for the purpose of
        // not breaking existing code
        assertFalse(query.contains("FROM `words`"));
    }

    /*
     * Boiler Code Reduction Funtions
     */

    /**
     * Gets the generated base query for a model when only calling .query() on it.
     * @return
     */
    private String getBaseQuery(Class<? extends Model> clazz) {
        return Repo.get(clazz).query().getQueryString().getQuery();
    }
}
