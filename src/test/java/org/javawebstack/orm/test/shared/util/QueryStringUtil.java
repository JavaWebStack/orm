package org.javawebstack.orm.test.shared.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.javawebstack.orm.test.shared.knowledge.QueryKnowledgeBase.QUOTE_CHARACTERS;
import static org.javawebstack.orm.test.shared.knowledge.QueryKnowledgeBase.TOP_LEVEL_KEYWORDS;
import static org.junit.jupiter.api.Assertions.fail;

public class QueryStringUtil {

    String queryString;

    public QueryStringUtil(String queryString) {
        this.queryString = queryString;
    }


    public List<String> getTopLevelSectionsByKeyword(String topLevelKeyword) {
        String capitalizedKeyword = topLevelKeyword.toUpperCase(Locale.ROOT);
        String queryString = this.queryString;

        List<String> sections = new LinkedList<String>();

        SectionInfo sectionInfo;
        do {
            sectionInfo = this.getNextTopLevelSectionByKeyword(queryString, topLevelKeyword);
            if (sectionInfo != null) {
                sections.add(sectionInfo.getSectionString());
                queryString = queryString.substring(sectionInfo.getEndIndex());
            }
        } while (sectionInfo != null);

        return sections;
    }
    private SectionInfo getNextTopLevelSectionByKeyword(String queryString, String topLevelKeyword) {
        String capitalizedKeyword = topLevelKeyword.toUpperCase(Locale.ROOT);
        String capitalizedQueryString = queryString.toUpperCase(Locale.ROOT);

        if(!TOP_LEVEL_KEYWORDS.contains(capitalizedKeyword))
            fail(String.format("Section name %s is not supported.", capitalizedKeyword));

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

            if (QUOTE_CHARACTERS.contains(currentCharacter)) {
                insideQuote = true;
                lastQuoteChar = currentCharacter;
                continue;
            }

            if ((queryString.length() - i) <= capitalizedKeyword.length())
                break;

            if (
                    startIndex == -1 &&
                    String.valueOf(currentCharacter).equalsIgnoreCase(String.valueOf(capitalizedKeyword.charAt(0))) &&
                    capitalizedQueryString.substring(i).startsWith(capitalizedKeyword)
            ) {
                // +1 skips the white space after the section name
                startIndex = i + capitalizedKeyword.length() + 1;
                // The -1 is to counteract the increment after the loop
                i = startIndex - 1;

                continue;
            }

            if (startIndex != -1 && this.checkQueryStringStartsWithSectionName(capitalizedQueryString.substring(i))) {
                endIndex = i - 1;
                break;
            }
        }

        if (startIndex == -1) {
            return null;
        }

        if (endIndex == -1)
            return new SectionInfo(queryString.substring(startIndex), startIndex, queryString.length());
        else
            return new SectionInfo(queryString.substring(startIndex, endIndex), startIndex, endIndex);
    }


    public boolean checkQueryStringStartsWithSectionName(String partialQueryString) {
        for (String singleSectionName : TOP_LEVEL_KEYWORDS)
            if(partialQueryString.toUpperCase(Locale.ROOT).startsWith(singleSectionName))
                return true;

        return false;
    }

    @Getter
    @AllArgsConstructor
    private static class SectionInfo {
        String sectionString;
        int startIndex;
        int endIndex;
    }
}
