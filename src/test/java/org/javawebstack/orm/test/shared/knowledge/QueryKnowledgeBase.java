package org.javawebstack.orm.test.shared.knowledge;

import java.util.Arrays;
import java.util.HashSet;

/**
 * The QueryKnowledgeBase serves as a decentralized information container around raw query terms.
 */
public class QueryKnowledgeBase {

    /**
     * Top level select keyword are SQL keywords which occur in SELECT statements and do not depend on another keyword
     * except for SELECT and FROM (which are both included in this set as well).
     * For example JOIN can appear after FROM statement so it is included. The ON keyword depends on a JOIN keyword though
     * which we view as a sub keyword of JOIN and therefore not as a top level keyword.
     */
    public static final HashSet<String> TOP_LEVEL_SELECT_KEYWORDS;

    /**
     * Quote characters are characters which prevents an SQL parser from picking up on a keyword, if the
     * wrap the keyword.
     */
    public static final HashSet<Character> QUOTE_CHARACTERS;


    static {
        TOP_LEVEL_SELECT_KEYWORDS = new HashSet<>(Arrays.asList(
                "SELECT",
                "FROM",
                "WHERE",
                "ORDER BY",
                "JOIN",
                "JOIN LEFT",
                "JOIN RIGHT",
                "INNER JOIN",
                "FULL JOIN",
                "FULL OUTER JOIN",
                "OUTER JOIN",
                "GROUP BY",
                "HAVING",
                "LIMIT",
                "OFFSET"
        ));

        QUOTE_CHARACTERS = new HashSet<>(Arrays.asList(
                '`',
                '\''
        ));
    }
}
