package org.javawebstack.orm.test.querybuilding;

import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.test.shared.models.Datatype;
import org.javawebstack.orm.test.shared.verification.QueryVerification;
import org.junit.jupiter.api.Test;

import static org.javawebstack.orm.test.shared.setup.ModelSetup.setUpModel;

public class OrderByClauseTest {

    @Test
    void testOneExistingColumnDefaultOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapper_integer");
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`wrapper_integer` ASC");
    }

    @Test
    void testOneNonExistingColumnDefaultOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("does_not_exist");
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`does_not_exist` ASC");
    }

    @Test
    void testOneExistingColumnASCOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapper_integer", false);
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`wrapper_integer` ASC");
    }

    @Test
    void testOneNonExistingColumnASCOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("does_not_exist", false);
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`does_not_exist` ASC");
    }

    @Test
    void testOneExistingColumnDESCOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapper_integer", true);
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`wrapper_integer` DESC");
    }

    @Test
    void testOneNonExistingColumnDESCOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("does_not_exist", true);
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`does_not_exist` DESC");
    }

    @Test
    void testMultipleOrderByClausesOfASCOrder() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapper_integer")
                .order("primitive_integer");

        new QueryVerification(query)
                .assertSectionContains("ORDER BY", "`wrapper_integer` ASC")
                .assertSectionContains("ORDER BY", "`primitive_integer` ASC");
    }

    @Test
    void testMultipleOrderByClausesOfDESCOrder() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapper_integer")
                .order("primitive_integer");

        new QueryVerification(query)
                .assertSectionContains("ORDER BY", "`wrapper_integer` DESC")
                .assertSectionContains("ORDER BY", "`primitive_integer` DESC");
    }

    @Test
    void testMultipleOrderByClausesOfMixedOrder() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapper_integer", false)
                .order("primitive_integer", true);

        new QueryVerification(query)
                .assertSectionContains("ORDER BY", "`wrapper_integer` ASC")
                .assertSectionContains("ORDER BY", "`primitive_integer` DESC");
    }

    @Test
    void testMultipleOrderByClausesOfMixedOrderReversed() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("primitive_integer", true)
                .order("wrapper_integer", false);

        new QueryVerification(query)
                .assertSectionContains("ORDER BY", "`primitive_integer` DESC")
                .assertSectionContains("ORDER BY", "`wrapper_integer` ASC");
    }

}
