package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.util.*;

/**
 * See the GrandTestAuto test specification.
 */
public class ExceptionHandling extends FTBase {
    public static Map<String, String> classNameToErrorKeyToThrow;

    public static String getErrorKeyToThrow( Class testClass ) {
        testsRun.add( testClass.getName() );
        return classNameToErrorKeyToThrow.get( testClass.getName() );
    }

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

        classNameToErrorKeyToThrow = new HashMap<String, String>();
        classNameToErrorKeyToThrow.put( "a44.a.test.UnitTester", "NPE First error" );
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Unit tests fail overall.
        assert !Helpers.overallUnitTestResultInLogFile();
        //Function tests pass overall.
        assert Helpers.overallFunctionTestResultInLogFile();
        //Load tests pass overall.
        assert Helpers.overallLoadTestResultInLogFile();
        //Error did not prevent further packages from running.
        assert testsRun.size() == 9;
        assert testsRun.containsAll( expectedTestsRun );

        init();
        classNameToErrorKeyToThrow = new HashMap<String, String>();
        classNameToErrorKeyToThrow.put( "a44.a.test.UnitTester", "Assertion error" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Unit tests fail overall.
        assert !Helpers.overallUnitTestResultInLogFile();
        //Function tests pass overall.
        assert Helpers.overallFunctionTestResultInLogFile();
        //Load tests pass overall.
        assert Helpers.overallLoadTestResultInLogFile();
        //Error did not prevent further packages from running.
        assert testsRun.size() == 9;
        assert testsRun.containsAll( expectedTestsRun );

        init();
        classNameToErrorKeyToThrow = new HashMap<String, String>();
        classNameToErrorKeyToThrow.put( "a44.a.functiontest.FT", "NPE for function tests" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Unit tests pass overall.
        assert Helpers.overallUnitTestResultInLogFile();
        //Function tests fail overall.
        assert !Helpers.overallFunctionTestResultInLogFile();
        //Load tests pass overall.
        assert Helpers.overallLoadTestResultInLogFile();
        //Error did not prevent further packages from running.
        assert testsRun.size() == 9;
        assert testsRun.containsAll( expectedTestsRun );

        init();
        classNameToErrorKeyToThrow = new HashMap<String, String>();
        classNameToErrorKeyToThrow.put( "a44.a.functiontest.FT", "Assertion error" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Unit tests pass overall.
        assert Helpers.overallUnitTestResultInLogFile();
        //Function tests fail overall.
        assert !Helpers.overallFunctionTestResultInLogFile();
        //Load tests pass overall.
        assert Helpers.overallLoadTestResultInLogFile();
        //Error did not prevent further packages from running.
        assert testsRun.size() == 9;
        assert testsRun.containsAll( expectedTestsRun );

        init();
        classNameToErrorKeyToThrow = new HashMap<String, String>();
        classNameToErrorKeyToThrow.put( "a44.a.loadtest.LT", "NPE for load tests" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Unit tests pass overall.
        assert Helpers.overallUnitTestResultInLogFile();
        //Function tests pass overall.
        assert Helpers.overallFunctionTestResultInLogFile();
        //Load tests fail overall.
        assert !Helpers.overallLoadTestResultInLogFile();
        //Error did not prevent further packages from running.
        assert testsRun.size() == 9;
        assert testsRun.containsAll( expectedTestsRun );

        init();
        classNameToErrorKeyToThrow = new HashMap<String, String>();
        classNameToErrorKeyToThrow.put( "a44.a.loadtest.LT", "Assertion error" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true );
        //Overall result false.
        assert !gta.runAllTests();
        //Unit tests pass overall.
        assert Helpers.overallUnitTestResultInLogFile();
        //Function tests pass overall.
        assert Helpers.overallFunctionTestResultInLogFile();
        //Load tests fail overall.
        assert !Helpers.overallLoadTestResultInLogFile();
        //Error did not prevent further packages from running.
        assert testsRun.size() == 9;
        assert testsRun.containsAll( expectedTestsRun );

        return true;
    }
}
