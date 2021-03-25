package org.javawebstack.orm.test.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.javawebstack.orm.test.shared.util.QueryStringUtil;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


/*
 * The scope of this class is to test the QueryUtility as it will be used to write other tests.
 */
class QueryUtilTest {

    @Test
    void testGetSectionSimple() {
        List<SectionRecord> list = new LinkedList<>(Arrays.asList(
            new SectionRecord("SELECT", RandomStringUtils.randomAlphanumeric(3, 12)),
            new SectionRecord("FROM", RandomStringUtils.randomAlphanumeric(3, 10)),
            new SectionRecord("WHERE", RandomStringUtils.randomAlphanumeric(3, 10)),
            new SectionRecord("ORDER BY", RandomStringUtils.randomAlphanumeric(3, 10)),
            new SectionRecord("GROUP BY", RandomStringUtils.randomAlphanumeric(3, 10)),
            new SectionRecord("HAVING", RandomStringUtils.randomAlphanumeric(3, 10)),
            new SectionRecord("LIMIT", RandomStringUtils.randomNumeric(1, 100)),
            new SectionRecord("OFFSET", RandomStringUtils.randomNumeric(1, 100))
        ));

        this.performStandardTestOnList(list);

    }

    @Test
    void testCasingDoesNotMatter() {
        List<SectionRecord> list = new LinkedList<>(Arrays.asList(
                new SectionRecord("select", RandomStringUtils.randomAlphanumeric(3, 12)),
                new SectionRecord("fRom", RandomStringUtils.randomAlphanumeric(3, 10)),
                new SectionRecord("where", RandomStringUtils.randomAlphanumeric(3, 10)),
                new SectionRecord("ordEr by", RandomStringUtils.randomAlphanumeric(3, 10)),
                new SectionRecord("Group By", RandomStringUtils.randomAlphanumeric(3, 10)),
                new SectionRecord("hAvIng", RandomStringUtils.randomAlphanumeric(3, 10)),
                new SectionRecord("limit", RandomStringUtils.randomNumeric(1, 100)),
                new SectionRecord("offset", RandomStringUtils.randomNumeric(1, 100))
        ));

        this.performStandardTestOnList(list);
    }

    // Example from here: https://www.freecodecamp.org/news/sql-example/
    @Test
    void testGetUsualCase() {
        List<SectionRecord> list = new LinkedList<>(Arrays.asList(
                new SectionRecord("SELECT", "`Customers`.`CustomerName`, `Orders`.`OrderID`"),
                new SectionRecord("FROM", "`Customers`"),
                new SectionRecord("FULL OUTER JOIN", "`Orders` ON `Customers`.`CustomerID`=`Orders`.`CustomerID`"),
                new SectionRecord("ORDER BY", "`Customers`.`CustomerName`")
        ));

        this.performStandardTestOnList(list);
    }

    @Test
    void testTrapExpressionsWhichAreEscaped() {
        List<SectionRecord> list = new LinkedList<>(Arrays.asList(
                new SectionRecord("SELECT", "`FROM`.`SELECT`, `GROUP BY`.`having`"),
                new SectionRecord("FROM", "`order by`"),
                new SectionRecord("WHERE", "`from` LIKE `where` AND 'from' = 'where'"),
                new SectionRecord("FULL OUTER JOIN", "`from` ON `FROM`.`SELECT`=`select`.`from`"),
                new SectionRecord("ORDER BY", "`limit`.`where`")
        ));

        this.performStandardTestOnList(list);
    }

    @Test
    void testMultipleOccurrences() {
        List<SectionRecord> list = new LinkedList<>(Arrays.asList(
                new SectionRecord("SELECT", RandomStringUtils.randomAlphanumeric(3, 12)),
                new SectionRecord("FROM", RandomStringUtils.randomAlphanumeric(3, 12)),
                new SectionRecord("JOIN", RandomStringUtils.randomAlphanumeric(3, 12)),
                new SectionRecord("JOIN", RandomStringUtils.randomAlphanumeric(3, 12)),
                new SectionRecord("JOIN", RandomStringUtils.randomAlphanumeric(3, 12))
        ));

        String queryString = this.getQueryStringFromList(list);
        QueryStringUtil util = new QueryStringUtil(queryString);

        SectionRecord currentRecord = list.get(0);
        assertEquals(currentRecord.getValue(), util.getTopLevelSectionsByKeyword(currentRecord.getKey()).get(0));

        currentRecord = list.get(1);
        assertEquals(currentRecord.getValue(), util.getTopLevelSectionsByKeyword(currentRecord.getKey()).get(0));

        currentRecord = list.get(2);
        assertEquals(currentRecord.getValue(), util.getTopLevelSectionsByKeyword(currentRecord.getKey()).get(0));

        currentRecord = list.get(3);
        assertEquals(currentRecord.getValue(), util.getTopLevelSectionsByKeyword(currentRecord.getKey()).get(1));

        currentRecord = list.get(4);
        assertEquals(currentRecord.getValue(), util.getTopLevelSectionsByKeyword(currentRecord.getKey()).get(2));

    }
    /*
     * Boilerplate Code Reduction Methods
     */

    private String getQueryStringFromList(List<SectionRecord> list) {
        StringBuilder builder = new StringBuilder();

        for(SectionRecord entry : list)
            builder
                .append(entry.getKey())
                .append(" ")
                .append(entry.getValue())
                .append(" ");

        return builder.toString().trim();
    }

    /*
     * The standard test in this case will be that each section cnly exists once and as defined per list.
     */
    private void performStandardTestOnList(List<SectionRecord> list) {
        String query = this.getQueryStringFromList(list);
        QueryStringUtil verification = new QueryStringUtil(query);

        for (SectionRecord entry : list) {
            List<String> foundSections = verification.getTopLevelSectionsByKeyword(entry.getKey());
            String firstSection = foundSections.get(0);
            assertEquals(1, foundSections.size(), "More than one section or no section was found, but only one unique section was expected.");
            assertEquals(
                    entry.getValue(),
                    firstSection,
                    String.format(
                        "The section name %s has been %s instead of %s.",
                        entry.getKey(),
                        firstSection,
                        entry.getValue()
                    )
            );
        }
    }

    @Getter
    @AllArgsConstructor
    static class SectionRecord {
        String key;
        String value;
    }

}
