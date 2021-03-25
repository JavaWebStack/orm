package org.javawebstack.orm.test.shared.verification;

import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.test.shared.util.QueryStringUtil;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class QueryVerification {

    HashSet<String> sectionNames;
    HashSet<Character> quoteCharacters;

    Query<?> query;

    public QueryVerification(Query<?> query) {
        this.query = query;

    }

    public void assertSectionContains(String sectionName, String containedSubstring) {
        this.assertSectionContains(sectionName, containedSubstring, 0);
    }

    public void assertSectionContains(String sectionName, String containedSubstring, int sectionIndex) {
        String sectionString;

        try {
            sectionString = new QueryStringUtil(this.query.getQueryString().getQuery())
                    .getTopLevelSectionsByKeyword(sectionName)
                    .get(sectionIndex);
        } catch (IndexOutOfBoundsException ignored) {
            fail(String.format(
                "A top level section of type %s and index %d was tested but only %d sections of that type existed.",
                sectionIndex,
                sectionName,
                sectionIndex + 1
            ));

            return;
        }

        assertTrue(
            sectionString.contains(containedSubstring),
            String.format("The occurrence of index %d of %s section of the query did not contain a substring %s but looked like this: %s. Note that the match is case-sensitive.", sectionIndex, sectionName, containedSubstring, sectionString)
        );
    }

}
