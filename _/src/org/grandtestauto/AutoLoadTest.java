package org.grandtestauto;

/**
 * Function and Load tests to be run automatically by GrandTestAuto
 * should implement this interface and be in a package with
 * name ending ".functiontest" or ".loadtest". Additionally, they
 * should have a public no-args constructor.
 */
public interface AutoLoadTest {
    public boolean runTest() throws Exception;
}
