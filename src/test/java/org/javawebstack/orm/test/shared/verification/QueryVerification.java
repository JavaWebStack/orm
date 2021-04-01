package org.javawebstack.orm.test.shared.verification;

import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.test.shared.util.QueryStringUtil;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * The QueryVerification class wraps an JWS Query Object and provides assertion methods regarding the raw query string.
 * For entered values it will however use a ? as they are retrieved from the prepared statement phase. No validation beyond the
 * assertions are made.
 */
public class QueryVerification {

    HashSet<String> sectionNames;
    HashSet<Character> quoteCharacters;

    Query<?> query;

    public QueryVerification(Query<?> query) {
        this.query = query;

    }

    /**
     * Asserts that in the first occurring section of a given top level keyword, the given string is contained.
     * This method uses the String.contains method internally and is therefore case sensitive.
     *
     * @param topLevelKeyword The top level keyword that prefaces the section.
     * @param containedSubstring The substring which should be contained in the first section of the given type.
     */
    public void assertSectionContains(String topLevelKeyword, String containedSubstring) {
        this.assertSectionContains(topLevelKeyword, containedSubstring, 0);
    }

    /**
     * Asserts that in the i-th occurring section of a given top level keyword, the given string is contained.
     * This method uses the String.contains method internally and is therefore case sensitive.
     *
     * @param topLevelKeyword The top level keyword that prefaces the section.
     * @param containedSubstring The substring which should be contained in the first section of the given type.
     * @param sectionIndex The index of the section to be checked, thusly 0 refers to the first occurrence etc.
     */
    public void assertSectionContains(String topLevelKeyword, String containedSubstring, int sectionIndex) {
        String sectionString;

        try {
            sectionString = new QueryStringUtil(this.query.getQueryString().getQuery())
                    .getTopLevelSectionsByKeyword(topLevelKeyword)
                    .get(sectionIndex);
        } catch (IndexOutOfBoundsException ignored) {
            fail(String.format(
                "A top level section of type %s and index %d was tested but only %d sections of that type existed.",
                sectionIndex,
                topLevelKeyword,
                sectionIndex + 1
            ));

            return;
        }

        assertTrue(
            sectionString.contains(containedSubstring),
            String.format("The occurrence of index %d of %s section of the query did not contain a substring %s but looked like this: %s. Note that the match is case-sensitive.", sectionIndex, topLevelKeyword, containedSubstring, sectionString)
        );
    }

}
