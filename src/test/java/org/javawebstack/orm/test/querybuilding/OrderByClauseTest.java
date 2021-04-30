package org.javawebstack.orm.test.querybuilding;

import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.test.exception.SectionIndexOutOfBoundException;
import org.javawebstack.orm.test.shared.models.Datatype;
import org.javawebstack.orm.test.shared.models.columnnames.OverwrittenColumnName;
import org.javawebstack.orm.test.shared.verification.QueryVerification;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.javawebstack.orm.test.shared.setup.ModelSetup.setUpModel;
import static org.junit.jupiter.api.Assertions.*;

// This class tests the query generation for order by statements an MySQL
class OrderByClauseTest {

    @Test
    void testOneExistingColumnDefaultOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapperInteger");
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`wrapper_integer`");
    }

    @Test
    void testOneNonExistingColumnDefaultOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("doesNotExist");

        // Not in snake case as it is not in the mapping
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`doesNotExist`");
    }

    @Test
    void testOneExistingColumnASCOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapperInteger", false);
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`wrapper_integer`");
    }

    @Test
    void testOneNonExistingColumnASCOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("doesNotExist", false);

        // Not in snake case as it is not in the mapping
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`doesNotExist`");
    }

    @Test
    void testOneExistingColumnDESCOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapperInteger", true);
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`wrapper_integer` DESC");
    }

    @Test
    void testOneNonExistingColumnDESCOrderBy() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("doesNotExist", true);
        // Not in snake case as it is not in the mapping
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`doesNotExist` DESC");
    }

    @Test
    void testMultipleOrderByClausesOfASCOrder() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapperInteger")
                .order("primitiveInteger");

        new QueryVerification(query)
                .assertSectionContains("ORDER BY", "`wrapper_integer`")
                .assertSectionContains("ORDER BY", "`primitive_integer`");
    }

    @Test
    void testMultipleOrderByClausesOfDESCOrder() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapperInteger", true)
                .order("primitiveInteger", true);

        new QueryVerification(query)
                .assertSectionContains("ORDER BY", "`wrapper_integer` DESC")
                .assertSectionContains("ORDER BY", "`primitive_integer` DESC");
    }

    @Test
    void testMultipleOrderByClausesOfMixedOrder() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("wrapperInteger", false)
                .order("primitiveInteger", true);

        new QueryVerification(query)
                .assertSectionContains("ORDER BY", "`wrapper_integer`")
                .assertSectionContains("ORDER BY", "`primitive_integer` DESC");
    }

    @Test
    void testMultipleOrderByClausesOfMixedOrderReversed() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("primitiveInteger", true)
                .order("wrapperInteger", false);

        new QueryVerification(query)
                .assertSectionContains("ORDER BY", "`primitive_integer` DESC")
                .assertSectionContains("ORDER BY", "`wrapper_integer`");
    }


    @Test
    // This test is important because putting the order by statements in different order is relevant (they set priorities)
    void testMultipleOrderByClausesOfRandomOrderForCorrectOrder() throws SectionIndexOutOfBoundException {
        // This test does not use camel cases as input
        Query<Datatype> query = setUpModel(Datatype.class).query();
        ArrayList<String> columnNames = new ArrayList<>(Datatype.columnNames);

        LinkedList<String> callOrder = new LinkedList<>();

        Random r = new Random();
        columnNames.stream().unordered().forEach((singleColumn) -> {
            query.order(singleColumn, r.nextBoolean());
            callOrder.add(singleColumn);
        });

        String queryString = new QueryVerification(query).getSection("ORDER BY");
        int lastIndex = 0;
        int foundIndex;
        for (String nextInCallOrder : callOrder) {
            foundIndex = queryString.indexOf("`" + nextInCallOrder + "`");
            if(foundIndex < lastIndex) {
                if (foundIndex == -1)
                    fail("Not all columns occurred in the query string.");
                else
                    fail("The columns did not appear an the correct order.");

                break;
            }

            lastIndex = foundIndex;
        }

        // If it came until here the test should count as passed.
        assertTrue(true);

    }

    @Test
    void testWillUseOverwrittenColumnName() {
        Query<OverwrittenColumnName> query = setUpModel(OverwrittenColumnName.class).query()
                .order("dummyString");
        new QueryVerification(query).assertSectionEquals("ORDER BY", "`oVer_writtenColumn-name`");
    }

    /*
     * Error Cases
     */

    // This test might not be correct here as it does not purely look at the query
    @Test
    void testCannotCallOrderOnSameColumnTwice() {
        Query<Datatype> query = setUpModel(Datatype.class).query()
                .order("primitiveInteger", true);

        assertThrows(ORMQueryException.class, () -> query.order("primitiveInteger"));
    }
}
