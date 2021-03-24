package org.javawebstack.orm.test.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.test.shared.models.Datatype;
import org.javawebstack.orm.test.shared.util.QueryStringUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.javawebstack.orm.test.shared.setup.ModelSetup.setUpModel;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        this.performeTestOnList(list);

    }

    @Test
    void testCasingDoesNotMatte() {
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

        this.performeTestOnList(list);
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

        this.performeTestOnList(list);
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

        this.performeTestOnList(list);
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

    private void performeTestOnList(List<SectionRecord> list) {
        String query = this.getQueryStringFromList(list);
        QueryStringUtil verification = new QueryStringUtil(query);
        for (SectionRecord entry : list)
            assertEquals(
                    entry.getValue(),
                    verification.getSectionByName(entry.getKey()),
                    String.format("The section name %s has been %s instead of %s.", entry.getKey(), verification.getSectionByName(entry.getKey()), entry.getValue())
            );
    }

    @Getter
    @AllArgsConstructor
    static class SectionRecord {
        String key;
        String value;
    }

}
