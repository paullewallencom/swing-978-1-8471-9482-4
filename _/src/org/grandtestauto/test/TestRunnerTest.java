package org.grandtestauto.test;

import java.util.*;

import org.grandtestauto.*;
import jet.testtools.test.org.grandtestauto.*;

/**
 * Unit test for <code>TestRunner</code>.
 *
 * @author Tim Lavers
 */
public class TestRunnerTest {

    private static Set<String> methodsCalled;

    public static void testMethodCalled( String methodName ) {
        methodsCalled.add( methodName );
    }

    public boolean mainTest() throws Exception {
        init();
        TestRunner.main( new String[] { "a7.test.XTest" } );
        //Check that the expected methods have been called.
        Helpers.assertEqual( methodsCalled.size(), 2 );
        Set<String> expected = new HashSet<String>();
        expected.add( "mTest" );
        expected.add( "nTest" );
        Helpers.assertEqual( methodsCalled, expected );

        //Test when just some methods called.
        init();
        TestRunner.main( new String[] { "a7.test.XTest", "mTest" } );
        assert methodsCalled.size() == 1;
        assert methodsCalled.contains( "mTest" );

        init();
        TestRunner.main( new String[] { "a7.test.XTest", "nTest" } );
        assert methodsCalled.size() == 1;
        assert methodsCalled.contains( "nTest" );

        return true;
    }

    public boolean badMethodNameTest() throws Exception {
        init();
        TestRunner.main( new String[] { "a7.test.XTest", "notATest" } );
        assert methodsCalled.size() == 0;

        return true;
    }

    private void init() throws Exception {
        methodsCalled = new HashSet<String>();
        //Expand the zip archive test7 into the temp directory.
        Helpers.cleanTempDirectory();
        Helpers.setupForZip( Grandtestauto.test7_zip );
    }
}