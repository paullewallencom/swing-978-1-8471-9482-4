package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.util.*;

/**
 * See the GrandTestAuto test specification.
 */
public class TestOutput extends FTBase {
    private static Set<String> classesToReturnFalse;

    public static boolean getResult( Class testClass ) {
        return !classesToReturnFalse.contains( testClass.getName() );
    }

    public boolean runTest() {
        //A single UnitTester returns false.
        classesToReturnFalse = new HashSet<String>();
        classesToReturnFalse.add( "a43.a.test.UnitTester" );
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test43_zip ), true, true, true );
        assert !gta.runAllTests();
        assert !Helpers.overallUnitTestResultInLogFile();
        assert Helpers.overallFunctionTestResultInLogFile();
        assert Helpers.overallLoadTestResultInLogFile();

        //A single Function Test returns false.
        classesToReturnFalse = new HashSet<String>();
        classesToReturnFalse.add( "a43.a.functiontest.FT" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test43_zip ), true, true, true );
        assert !gta.runAllTests();
        assert Helpers.overallUnitTestResultInLogFile();
        assert !Helpers.overallFunctionTestResultInLogFile();
        assert Helpers.overallLoadTestResultInLogFile();

        //A single Load Test returns false.
        classesToReturnFalse = new HashSet<String>();
        classesToReturnFalse.add( "a43.a.loadtest.LT" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test43_zip ), true, true, true );
        assert !gta.runAllTests();
        assert Helpers.overallUnitTestResultInLogFile();
        assert Helpers.overallFunctionTestResultInLogFile();
        assert !Helpers.overallLoadTestResultInLogFile();

        //Every test returns true.
        classesToReturnFalse = new HashSet<String>();
        gta = Helpers.setupForZip( new File( Grandtestauto.test43_zip ), true, true, true );
        assert gta.runAllTests();
        assert Helpers.overallUnitTestResultInLogFile();
        assert Helpers.overallFunctionTestResultInLogFile();
        assert Helpers.overallLoadTestResultInLogFile();
        return true;
    }
}
