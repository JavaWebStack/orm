package org.javawebstack.orm.test.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Only to be used for tests.
 * This exception should be thrown when a SQL Query String is manually parsed and sections and section types are defined, and
 * a type of section is attempted to be retrieved which does not exist in this number.
 */
public class SectionIndexOutOfBoundException extends Exception {
    private int sectionCount;
    private int attemptedIndex;
    private String topLevelKeyword;
}
