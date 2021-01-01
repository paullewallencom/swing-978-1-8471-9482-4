package org.grandtestauto;

/**
 * Used for the classification of classes according
 * to the extent that they need unit testing.
 *
 * @author Tim Lavers
 */
public enum Testability {
    NO_TEST_REQUIRED, CONTAINS_TESTABLE_METHODS, TEST_REQUIRED
}
