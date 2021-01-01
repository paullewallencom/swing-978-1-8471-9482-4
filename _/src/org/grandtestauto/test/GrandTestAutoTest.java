package org.grandtestauto.test;

import org.grandtestauto.*;

import java.util.*;
import java.io.*;

import jet.testtools.test.org.grandtestauto.*;
import jet.testtools.*;

/**
 * Unit test for <code>GrandTestAuto</code>.
 *
 * @author Tim Lavers
 */
public class GrandTestAutoTest {

    /**
     * The names of the Unit Testers called within various sub-tests.
     */
    private static Set<String> utsCalled = new HashSet<String>();

    /**
     * The names of the Function Tests called within various sub-tests.
     */
    private static TreeSet<String> functionTestsCalled = new TreeSet<String>();

    /**
     * The names of the Load Tests called within various sub-tests.
     */
    private static TreeSet<String> loadTestsCalled = new TreeSet<String>();

    /**
     * Called from a test program run within a sub-test.
     */
    public static void unitTesterCalled( Class utClass ) {
        utsCalled.add( utClass.getName() );
    }

    /**
     * Called from the test programs.
     */
    public static void functionTestCalled( Class ftName ) {
        functionTestsCalled.add( ftName.getName() );
    }

    /**
     * Called from the test programs.
     */
    public static void loadTestCalled( Class ftName ) {
        loadTestsCalled.add( ftName.getName() );
    }

    public boolean constructorTest() {
        //runAllTestsTest exercises the constructor
        //sufficiently, so just return true.
        return true;
    }

    public boolean mainTest() {
        //Just returns true: the fact that these tests run at
        //all are proof that it works.
        return true;
    }

    public boolean runAllTestsTest() throws Exception {
        assert checkAllUnitTestersCalled();
        assert checkOneFailsResultFalse();
        assert checkAllSucceedResultTrue();
        return true;
    }

    public boolean resultsLoggerTest() {
        //A GTA that logs to the default log file.
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test1_zip ), true, false, false, null, false, true, null );
        String message = "Message1@" + System.currentTimeMillis();
        gta.resultsLogger().log( message, null);
        File log = new File( Settings.DEFAULT_LOG_FILE_NAME );
        assert Files.contents( log ).contains( message );
        gta.resultsLogger().closeLogger();

        //A GTA that logs to a specific file.
        log = new File( Helpers.tempDirectory(),  "GTATest.txt");
        gta = Helpers.setupForZip( new File( Grandtestauto.test1_zip ), true, false, false, null, false, true, log.getPath() );
        message = "Message2@" + System.currentTimeMillis();
        gta.resultsLogger().log( message, null);
        assert Files.contents( log ).contains( message );
        gta.resultsLogger().closeLogger();

        return true;
    }

    /**
     * Check that all of the UnitTester classes in a package hierarchy are called.
     */
    private boolean checkAllUnitTestersCalled() throws Exception {
        reset();
        //Expand the zip archive into the temp directory.
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test1_zip ), true, false, false );
        gta.runAllTests();
        //Now check to see which UnitTesters are called. This set should
        //contain just the following:
        // a1.b.e.g.test.Unit.Tester
        // a1.b.test.UnitTester
        // a1.c.h.test.UnitTester
        // a1.d.test.UnitTester
        // a1.test.UnitTester
        //The classes that are not included:
        // a1.c.UnitTester as there are no classes in a.c
        // a1.b.e.g.UnitTester as it's not in a test package.
        assert utsCalled.contains( "a1.b.e.g.test.UnitTester" );
        assert utsCalled.contains( "a1.b.test.UnitTester" );
        assert utsCalled.contains( "a1.c.h.test.UnitTester" );
        assert utsCalled.contains( "a1.d.test.UnitTester" );
        assert utsCalled.contains( "a1.test.UnitTester" );
        assert utsCalled.size() == 5;
        return true;
    }

    /**
     * Check that if one of the UnitTesters returns false, the overall result is false.
     */
    private boolean checkOneFailsResultFalse() throws Exception {
        reset();
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test2_zip )  );
        //Should return false.
        return !gta.runAllTests();
    }

    /**
     * Check that if each of the UnitTesters returns true, the overall result is true.
     */
    private boolean checkAllSucceedResultTrue() throws Exception {
        reset();
        //Expand the zip archive into the temp directory.
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test3_zip )  );
        //Should return true.
        return gta.runAllTests();
    }

    public boolean syntheticMethodsDoNotNeedTestingTest() throws Exception {
        reset();
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test18_zip )  );
        //Should return true.
        return gta.runAllTests();
    }

    public boolean runFunctionTestsTest() throws Exception {
        reset();
        GrandTestAuto gta = Helpers.setupForZip( Grandtestauto.test35_zip );
        boolean gtaResult = gta.runAllTests();
        TreeSet<String> expectedFTNames = new TreeSet<String>();
        expectedFTNames.add( "a35.b.functiontest.BugsBunny" );
        expectedFTNames.add( "a35.b.functiontest.DaffyDuck" );
        expectedFTNames.add( "a35.b.functiontest.ElmerFudd" );
        expectedFTNames.add( "a35.c.functiontest.DaffyDuck" );
        expectedFTNames.add( "a35.c.functiontest.YosemiteSam" );
        expectedFTNames.add( "a35.d.test.functiontest.DaffyDuck" );
        expectedFTNames.add( "a35.d.test.functiontest.FearlessFreep" );

        assert expectedFTNames.equals( functionTestsCalled ) : functionTestsCalled;
        assert gtaResult;
        return true;
    }

    public boolean runLoadTestsTest() throws Exception {
        reset();
        GrandTestAuto gta = Helpers.setupForZip( Grandtestauto.test37_zip );
        boolean gtaResult = gta.runAllTests();
        TreeSet<String> expectedLTNames = new TreeSet<String>();
        expectedLTNames.add( "a37.b.loadtest.BugsBunny" );
        expectedLTNames.add( "a37.b.loadtest.DaffyDuck" );
        expectedLTNames.add( "a37.b.loadtest.ElmerFudd" );
        expectedLTNames.add( "a37.c.loadtest.DaffyDuck" );
        expectedLTNames.add( "a37.c.loadtest.YosemiteSam" );
        expectedLTNames.add( "a37.d.test.loadtest.DaffyDuck" );
        expectedLTNames.add( "a37.d.test.loadtest.FearlessFreep" );

        assert expectedLTNames.equals( loadTestsCalled ) : loadTestsCalled;
        assert gtaResult;
        return true;
    }

    private void reset() {
        utsCalled = new HashSet<String>();
        functionTestsCalled = new TreeSet<String>();
        loadTestsCalled = new TreeSet<String>();
    }
}