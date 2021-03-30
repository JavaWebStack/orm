package org.javawebstack.orm.test.querybuilding;

import org.atteo.evo.inflector.English;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.tablenames.*;
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
        String query = getBaseQuery(Word.class);
        assertTrue(query.contains("FROM `words`"));
    }

    @Test
    void testTwoWordsBecomeSnakeCases() throws ORMConfigurationException {
        String query = getBaseQuery(TwoWord.class);
        assertTrue(query.contains("FROM `two_words`"));
    }

    @Test
    void testThreeWordBecomeSnakeCases() throws ORMConfigurationException {
        String query = getBaseQuery(ThreeWordClass.class);
        assertTrue(query.contains("FROM `three_word_classes`"));
    }

    @Test
    void testIrregularWordPlural() throws ORMConfigurationException {
        String query = getBaseQuery(Mouse.class);
        assertTrue(query.contains("FROM `mice`"));
    }

    /*
     * Error / Not Closer Specified Cases
     */
    @Test
    void testOneWordAlreadyInPluralDoesntWork() throws ORMConfigurationException {
        String query = getBaseQuery(Words.class);
        // Should try to find a non-sense plural and not map to itself as plural; for the purpose of
        // not breaking existing code
        assertFalse(query.contains("FROM `words`"));
    }

    @Test
    void testOverwrittenTableName() throws ORMConfigurationException {
        String query = getBaseQuery(OverwritteTableName.class);
        assertTrue(query.contains("FROM `oVer_writtenValue`"));
    }

    /*
     * Boiler Code Reduction Funtions
     */

    /**
     * Gets the generated base query for a model when only calling .query() on it.
     * @return
     */
    private String getBaseQuery(Class<? extends Model> clazz) throws ORMConfigurationException {
        ORM.register(clazz, sql());
        return Repo.get(clazz).getConnection().builder().buildQuery(Repo.get(clazz).query(), false).getQuery();
    }
}
