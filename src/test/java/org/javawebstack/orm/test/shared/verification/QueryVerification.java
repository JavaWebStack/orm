package org.javawebstack.orm.test.shared.verification;

import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.test.exception.SectionIndexOutOfBoundException;
import org.javawebstack.orm.test.shared.util.QueryStringUtil;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public QueryVerification assertSectionContains(String topLevelKeyword, String containedSubstring) {
        this.assertSectionContains(topLevelKeyword, containedSubstring, 0);
        return this;
    }

    /**
     * Asserts that in the i-th occurring section of a given top level keyword, the given string is contained.
     * This method uses the String.contains method internally and is therefore case sensitive.
     *
     * @param topLevelKeyword The top level keyword that prefaces the section.
     * @param containedSubstring The substring which should be contained in the first section of the given type.
     * @param sectionIndex The index of the section to be checked, thus 0 refers to the first occurrence etc.
     */
    public QueryVerification assertSectionContains(String topLevelKeyword, String containedSubstring, int sectionIndex) {
        String sectionString = null;
        try {
            sectionString = getSection(topLevelKeyword, sectionIndex);
        } catch (SectionIndexOutOfBoundException e) {
            this.failDueToSectionIndexOutOfBounds(e);
            return this;
        }

        assertTrue(
                sectionString.contains(containedSubstring),
                String.format("The occurrence of index %d of %s section of the query did not contain a substring %s but looked like this: %s. Note that the match is case-sensitive.", sectionIndex, topLevelKeyword, containedSubstring, sectionString)
        );

        return this;
    }

    /**
     * Asserts that the first occurring section of a given top level keyword is equal to the given string.
     * This method uses the String.equals method internally and is therefore case sensitive.
     *
     * @param topLevelKeyword The top level keyword that prefaces the section.
     * @param expectedString The substring which the first section of the given type should be equal to.
     */
    public QueryVerification assertSectionEquals(String topLevelKeyword, String expectedString) {
        this.assertSectionEquals(topLevelKeyword, expectedString, 0);
        return this;
    }

    /**
     * Asserts that the i-th occurring section of a given top level keyword equal to the given string.
     * This method uses the String.equals method internally and is therefore case sensitive.
     *
     * @param topLevelKeyword The top level keyword that prefaces the section.
     * @param expectedString The substring which the specified section of the given type should be equal to.
     * @param sectionIndex The index of the section to be checked, thus 0 refers to the first occurrence etc.
     */
    public QueryVerification assertSectionEquals(String topLevelKeyword, String expectedString, int sectionIndex) {
        String sectionString = null;
        try {
            sectionString = getSection(topLevelKeyword, sectionIndex);
        } catch (SectionIndexOutOfBoundException e) {
            this.failDueToSectionIndexOutOfBounds(e);
            return this;
        }

        assertEquals(
                expectedString,
                sectionString,
                String.format("The occurrence of index %d of %s section of the query was not equal to the string %s but looked like this: %s. Note that the match is case-sensitive.", sectionIndex, topLevelKeyword, expectedString, sectionString)
        );

        return this;
    }

    /**
     * Retrieves the inner part of a section by its keyword. With multiple occurrences it will only retrieve the first
     * one. It does not include the keyword and one whitespaces at start and end.
     *
     * @param topLevelKeyword The top level keyword that prefaces the section.
     * @return The inner part of the first section as specified.
     * @throws SectionIndexOutOfBoundException if no section by that top level keyword exists.
     */
    public String getSection(String topLevelKeyword) throws SectionIndexOutOfBoundException {
        return this.getSection(topLevelKeyword, 0);
    }

    /**
     * Retrieves the inner part of a section by its keyword and index specifying which occurrence should be retrieved.
     * It does not include the keyword and one whitespaces at start and end.
     *
     * @param topLevelKeyword The top level keyword that prefaces the section.
     * @param sectionIndex The index of the section to be retrieved, thus 0 refers to the first occurrence etc.
     * @return The inner part of the first section as specified.
     * @throws SectionIndexOutOfBoundException if there are less than sectionIndex + 1 elements
     */
    public String getSection(String topLevelKeyword, int sectionIndex) throws SectionIndexOutOfBoundException {
        List<String> sectionList = this.getSectionList(topLevelKeyword);
        try {
            return sectionList.get(sectionIndex);
        } catch (IndexOutOfBoundsException converted) {

            SectionIndexOutOfBoundException exception = new SectionIndexOutOfBoundException();

            exception.setSectionCount(sectionList.size());
            exception.setAttemptedIndex(sectionIndex);
            exception.setTopLevelKeyword(topLevelKeyword);

            throw exception;
        }
    }

    /**
     * Retrieve list of all sections prefaced with the specified top level keyword. The list will have the same order
     * as the occurrences of each section.
     *
     * @param topLevelKeyword The top level keyword that prefaces the sections.
     * @return The order sensitive string list of inner sections.
     */
    public List<String> getSectionList(String topLevelKeyword) {
        return new QueryStringUtil(this.query.getRepo().getConnection().builder().buildQuery(this.query).getQuery())
                .getTopLevelSectionsByKeyword(topLevelKeyword);
    }

    private void failDueToSectionIndexOutOfBounds(SectionIndexOutOfBoundException exception) {
        fail(String.format(
                "A top level section of type %s and index %d was tested but only %d sections of that type existed.",
                exception.getTopLevelKeyword(),
                exception.getAttemptedIndex(),
                exception.getSectionCount()
        ));
    }

}
