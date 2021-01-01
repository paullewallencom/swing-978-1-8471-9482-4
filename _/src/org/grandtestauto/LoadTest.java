package org.grandtestauto;

/**
 * Load tests to be run automatically by GrandTestAuto
 * should implement this interface and be in a package with
 * name ending ".loadtest".
 */
public interface LoadTest {
    public boolean runTest() throws Exception;
}
