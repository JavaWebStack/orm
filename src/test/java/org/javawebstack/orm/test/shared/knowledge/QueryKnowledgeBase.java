package org.javawebstack.orm.test.shared.knowledge;

import java.util.Arrays;
import java.util.HashSet;

public class QueryKnowledgeBase {

    public static final HashSet<String> TOP_LEVEL_KEYWORDS;
    public static final HashSet<Character> QUOTE_CHARACTERS;


    static {
        TOP_LEVEL_KEYWORDS = new HashSet<>(Arrays.asList(
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
