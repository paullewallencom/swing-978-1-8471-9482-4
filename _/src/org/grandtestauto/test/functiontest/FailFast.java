package org.grandtestauto.test.functiontest;

import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.util.*;
import java.io.*;

import jet.testtools.test.org.grandtestauto.*;

/**
 * See the GrandTestAuto test specification.
 */
public class FailFast extends FTBase {

    public boolean runTest() {
        Set<String> expectedTestsRun = new HashSet<String>();
        expectedTestsRun.add( "a44.a.test.UnitTester" );
        expectedTestsRun.add( "a44.b.test.UnitTester" );
        expectedTestsRun.add( "a44.c.test.UnitTester" );
        expectedTestsRun.add( "a44.a.functiontest.FT" );
        expectedTestsRun.add( "a44.b.functiontest.FT" );
        expectedTestsRun.add( "a44.c.functiontest.FT" );
        expectedTestsRun.add( "a44.a.loadtest.LT" );
        expectedTestsRun.add( "a44.b.loadtest.LT" );
        expectedTestsRun.add( "a44.c.loadtest.LT" );

        ExceptionHandling.classNameToErrorKeyToThrow = new HashMap<String, String>();
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.test.UnitTester", "NPE First error" );
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.functiontest.FT", "NPE for function tests" );
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.loadtest.LT", "Assertion error" );
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Unit tests fail overall.
        assert !Helpers.overallUnitTestResultInLogFile();
        //Function tests pass overall.
        assert !Helpers.overallFunctionTestResultInLogFile();
        //Load tests pass overall.
        assert !Helpers.overallLoadTestResultInLogFile();
        //Error did not prevent further packages from running.
        assert testsRun.size() == 9;
        assert testsRun.containsAll( expectedTestsRun );

        //Now run with fail fast.
        testsRun.clear();
        expectedTestsRun = new HashSet<String>();
        expectedTestsRun.add( "a44.a.test.UnitTester" );
        gta = Helpers.setupForZipWithFailFast( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Only a single test package run.
        assert testsRun.size() == 1: testsRun;
        assert testsRun.containsAll( expectedTestsRun );
        String logFileContents = Helpers.logFileContents();
        //Unit tests fail overall and this is recorded.
        assert !Helpers.overallUnitTestResultInLogFile();
        //Unit test running is curtailed, and this is recorded.
        assert logFileContents.contains( Messages.message( Messages.SK_FAIL_FAST_UNIT_TESTS  ) );
        //Function tests skipped, and this is recorded.
        assert logFileContents.contains( Messages.message( Messages.SK_FAIL_FAST_SKIP_FUNCTION_TESTS  ) );
        //Load tests skipped, and this is recorded.
        assert logFileContents.contains( Messages.message( Messages.SK_FAIL_FAST_SKIP_LOAD_TESTS  ) );

        //No unit test errors this time.
        ExceptionHandling.classNameToErrorKeyToThrow = new HashMap<String, String>();
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.functiontest.FT", "NPE for function tests" );
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.loadtest.LT", "Assertion error" );
        testsRun.clear();
        expectedTestsRun = new HashSet<String>();
        expectedTestsRun.add( "a44.a.test.UnitTester" );
        expectedTestsRun.add( "a44.b.test.UnitTester" );
        expectedTestsRun.add( "a44.c.test.UnitTester" );
        expectedTestsRun.add( "a44.a.functiontest.FT" );
        gta = Helpers.setupForZipWithFailFast( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Check the packages run.
        assert testsRun.size() == 4: testsRun;
        assert testsRun.containsAll( expectedTestsRun );
        logFileContents = Helpers.logFileContents();
        //Unit tests pass overall and this is recorded.
        assert Helpers.overallUnitTestResultInLogFile();
        //Function tests fail overall and this is recorded.
        assert !Helpers.overallFunctionTestResultInLogFile();
        //Some function tests skipped, and this is recorded.
        assert logFileContents.contains( Messages.message( Messages.SK_FAIL_FAST_FUNCTION_TESTS  ) );
        //Load tests skipped, and this is recorded.
        assert logFileContents.contains( Messages.message( Messages.SK_FAIL_FAST_SKIP_LOAD_TESTS  ) );

        //No unit or function test errors this time.
        ExceptionHandling.classNameToErrorKeyToThrow = new HashMap<String, String>();
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.loadtest.LT", "Assertion error" );
        testsRun.clear();
        expectedTestsRun = new HashSet<String>();
        expectedTestsRun.add( "a44.a.test.UnitTester" );
        expectedTestsRun.add( "a44.b.test.UnitTester" );
        expectedTestsRun.add( "a44.c.test.UnitTester" );
        expectedTestsRun.add( "a44.a.functiontest.FT" );
        expectedTestsRun.add( "a44.b.functiontest.FT" );
        expectedTestsRun.add( "a44.c.functiontest.FT" );
        expectedTestsRun.add( "a44.a.loadtest.LT" );
        gta = Helpers.setupForZipWithFailFast( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Check the packages run.
        assert testsRun.size() == 7: testsRun;
        assert testsRun.containsAll( expectedTestsRun );
        logFileContents = Helpers.logFileContents();
        //Unit tests pass over all and this is recorded.
        assert Helpers.overallUnitTestResultInLogFile();
        //Function tests pass over all and this is recorded.
        assert Helpers.overallFunctionTestResultInLogFile();
        //Load tests fail over all and this is recorded.
        assert !Helpers.overallLoadTestResultInLogFile();
        //Some load tests skipped, and this is recorded.
        assert logFileContents.contains( Messages.message( Messages.SK_FAIL_FAST_LOAD_TESTS  ) );
        return true;
    }
}
