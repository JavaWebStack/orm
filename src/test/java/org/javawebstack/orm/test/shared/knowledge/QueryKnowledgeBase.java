package org.javawebstack.orm.test.shared.knowledge;

import java.util.Arrays;
import java.util.HashSet;

public class QueryKnowledgeBase {

    public static HashSet<String> sectionNames;
    public static HashSet<Character> quoteCharacters;


    static {
        sectionNames = new HashSet<>(Arrays.asList(
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

        quoteCharacters = new HashSet<>(Arrays.asList(
                '`',
                '\''
        ));
    }
}
