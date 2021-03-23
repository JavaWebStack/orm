package org.javawebstack.orm.test.shared.util;

import java.util.Locale;

import static org.javawebstack.orm.test.shared.knowledge.QueryKnowledgeBase.quoteCharacters;
import static org.javawebstack.orm.test.shared.knowledge.QueryKnowledgeBase.sectionNames;
import static org.junit.jupiter.api.Assertions.fail;

public class QueryStringUtil {

    String queryString;

    public QueryStringUtil(String queryString) {
        this.queryString = queryString;
    }

    public String getSectionByName(String sectionName) {
        String queryString = this.queryString;
        String capitalizedQueryString = queryString.toUpperCase(Locale.ROOT);

        if(!sectionNames.contains(sectionName))
            fail(String.format("Section name %s is not supported.", sectionName));

        boolean insideQuote = false;
        char lastQuoteChar = ' ';
        int startIndex = -1;
        int endIndex = -1;

        char currentCharacter;


        for (int i = 0; i < queryString.length(); i++) {
            currentCharacter = queryString.charAt(i);

            if (insideQuote) {
                if (lastQuoteChar == currentCharacter)
                    insideQuote = false;

                continue;
            }

            if (quoteCharacters.contains(currentCharacter)) {
                insideQuote = true;
                lastQuoteChar = currentCharacter;
                continue;
            }

            if ((queryString.length() - i) <= sectionName.length())
                break;

            if (
                    startIndex == -1 &&
                    currentCharacter == sectionName.charAt(0) &&
                    capitalizedQueryString.substring(i).startsWith(sectionName)
            ) {
                startIndex = i + sectionName.length();
                // The -1 is to counteract the increment after the loop
                i = startIndex - 1;

                // if we find no other section assume whole rest of the string to be part
                endIndex = queryString.length() - 1;
                continue;
            }

            if (startIndex != -1 && this.checkItStartWithSectionName(capitalizedQueryString.substring(i))) {
                endIndex = i - 1;
                break;
            }
        }

        if (startIndex == -1) {
            return null;
        }

        return queryString.substring(startIndex, endIndex);
    }


    public boolean checkItStartWithSectionName(String partialQueryString) {
        for (String singleSectionName : sectionNames)
            if(partialQueryString.toUpperCase(Locale.ROOT).startsWith(singleSectionName))
                return true;

        return false;
    }
}
