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

class FromClauseTest extends ORMTestCase {

    @Test
    void testFromClauseWithOneWordSAppendixPlural() throws ORMConfigurationException {
        ORM.register(One.class, sql());
        String query = Repo.get(One.class).query().getQueryString().getQuery();
        assertTrue(query.contains("FROM `ones`"));
    }
}
