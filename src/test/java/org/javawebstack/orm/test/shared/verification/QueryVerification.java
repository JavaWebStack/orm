package org.javawebstack.orm.test.shared.verification;

import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.test.shared.util.QueryStringUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

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
        String sectionString = new QueryStringUtil(this.query.getQueryString().getQuery()).getSectionByName(sectionName);
        assertTrue(
            sectionString.contains(containedSubstring),
            String.format("The %s section of the query did not contain a substring %s but looked like this: %s. Note that the match is case-sensitive.", sectionName, containedSubstring, sectionString)
        );
    }

}
