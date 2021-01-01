package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

/**
 * The execution time for each AutoLoadTest is logged.
 */
public class TimesForAutoLoadTestsAreLogged extends FTBase {
    private Map<String, Number> testNameTimeTaken;

    /**
     * Run GTA for a configured package for which each test takes a known length
     * of time, and analyse the log file to show that the times are logged and are as expected.
     * 
     * Run GTA for the package a82. Run the function and load tests only.
     * This package has function tests  and load tests as follows:
     * <nl>a82.functiontest.A: pauses for 5500ms then returns true.
     * <nl>a82.functiontest.B: pauses for 2500ms then returns true
     * <nl>a82.functiontest.C: pauses for 500ms then returns true
     * <nl>a82.loadtest.X: pauses for 2005ms, then returns true.
     * <nl>a82.loadtest.Y: pauses for 205ms, then returns false.
     * <nl>a82.loadtest.Z: pauses for 50ms, then throws a NullPointerException.
     * <p/>
     * <nl> Read in the log file and find the actual time taken for each test.
     * <p/>
     * Now check the times to run the tests with those expected.
     */
    public boolean runTest() throws Exception {
        //Don't run the unit tests as the names ATest etc will confuse the algorithm below.
        Helpers.setupForZip( new File( Grandtestauto.test82_zip ), false, true, true ).runAllTests();
        testNameTimeTaken = new HashMap<String, Number>();

        //Find those lines starting with an AutoLoadTest name followed by a space and ending with an 's'.
        Pattern autoLoadTestRecogniser = Pattern.compile( "([A,B,C,X,Y,Z]) .* ([0-9]*[\\.]{0,1}[0-9]*)s" );
        NumberFormat numberFormatter = NumberFormat.getNumberInstance();
        BufferedReader br = new BufferedReader( new FileReader( Helpers.defaultLogFile() ) );
        String line = br.readLine();
        while (line != null) {
            Matcher matcher = autoLoadTestRecogniser.matcher( line );
            if (matcher.matches()) {
                String timeStr = matcher.group( 2 );
                Number seconds = numberFormatter.parse( timeStr );
                String classStr = matcher.group( 1 );
                testNameTimeTaken.put( classStr, seconds );
            }
            line = br.readLine();
        }
        br.close();

        check( "A", 5.5, .2 );
        check( "B", 2.5, .2 );
        check( "C", .5, .2 );
        check( "X", 2, .2 );
        check( "Y", .2, .2 );
        check( "Z", .05, .2 );
        return true;
    }

    private void check( String testName, double expected, double tolerance ) {
        Number timeTaken = testNameTimeTaken.get( testName );
        assert timeTaken.floatValue() < expected + tolerance;
        assert timeTaken.floatValue() > expected - tolerance;
    }
}
