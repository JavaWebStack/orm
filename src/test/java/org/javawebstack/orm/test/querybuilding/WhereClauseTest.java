package org.javawebstack.orm.test.querybuilding;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.Datatype;
import org.javawebstack.orm.test.shared.models.EmptyUUIDModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.javawebstack.orm.test.shared.setup.ModelSetup.setUpModel;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WhereClauseTest extends ORMTestCase {

    private Repo<Datatype> repo;

    @BeforeEach
    void setUp() {
        this.repo = setUpModel(Datatype.class);
    }

    /*
     * Normal Cases
     */

    @Test
    void testImplicitEqualOperation() {
        Query<Datatype> query = repo.where("wrapper_boolean", true);
        assertContainsAfterWhere("`wrapper_boolean` = ?",query.getQueryString().getQuery());
    }

    @Test
    void testImplicitEqualOperationReverse() {
        Query<Datatype> query = repo.where(true, "wrapper_boolean");
        assertContainsAfterWhere("? = `wrapper_boolean`",query.getQueryString().getQuery());
    }

    @Test
    void testImplicitEqualOperationWithWrongColumnName() {
        // Note the missing b
        Query<Datatype> query = repo.where("wrapper_oolean", true);
        assertContainsAfterWhere("? = ?", query.getQueryString().getQuery());
    }

    @Test
    void testExplicitEqualOperation() {
        Query<Datatype> query = repo.where("wrapper_boolean",  "=",true);
        assertContainsAfterWhere("`wrapper_boolean` = ?",query.getQueryString().getQuery());
    }

    @Test
    void testExplicitEqualOperationReverse() {
        Query<Datatype> query = repo.where(true, "=" , "wrapper_boolean");
        assertContainsAfterWhere("? = `wrapper_boolean`",query.getQueryString().getQuery());
    }

    @Test
    void testExplicitEqualOperationWithWrongColumnName() {
        // Note the missing b
        Query<Datatype> query = repo.where("wrapper_oolean", "=", true);
        assertContainsAfterWhere("? = ?", query.getQueryString().getQuery());
    }

    @Test
    void testImplicitEqualOnId() {
        Query<Datatype> query = repo.whereId(3);
        assertContainsAfterWhere("`id` = ?", query.getQueryString().getQuery());
    }

    @Test
    void testImplicitEqualOnUuid() throws ORMConfigurationException {
        ORM.register(EmptyUUIDModel.class, sql());
        Query<EmptyUUIDModel> query = ORM.repo(EmptyUUIDModel.class).whereId("unique-stuff");
        assertContainsAfterWhere("`uuid` = ?", query.getQueryString().getQuery());
    }

    @Test
    void testNonsenseOperation() {
        Query<Datatype> query = repo.where("wrapper_boolean", "NOOPERATION", true);
        assertContainsAfterWhere("`wrapper_boolean` NOOPERATION ?", query.getQueryString().getQuery());
    }



    private void assertContainsAfterWhere(String containedString, String completeQuery) {
        String whereClause = completeQuery.split("WHERE")[1];
        assertTrue(whereClause.contains(containedString), "Expected the query to contain the string " + containedString + " after the WHERE clause, but got this: " + whereClause);

    }
}
