/****************************************************************************
 *
 * Name: GrandTestAuto.java
 *
 * Synopsis: See javadoc class comments.
 *
 * Description: See javadoc class comments.
 *
 * Copyright 2002 Timothy Gordon Lavers (Australia)
 *
 *                          The Wide Open License (WOL)
 *
 * Permission to use, copy, modify, distribute and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice and this license appear in all source copies.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT EXPRESS OR IMPLIED WARRANTY OF
 * ANY KIND. See http://www.dspguru.com/wol.htm for more information.
 *
 *****************************************************************************/
package org.grandtestauto;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * <code>GrandTestAuto</code> is used to run tests for a java project
 * consisting of class files contained in a directory and its subdirectories.
 *
 * @author Tim Lavers
 */
public class GrandTestAuto {

    private static final String VERSION = "GrandTestAuto 3.3";

    /**
     * The runtime parameters.
     */
    private Settings settings;

    private ResultsLogger resultsLogger;

    /**
     * Information about the packages in the project.
     */
    private PackagesInfo<PackageInfo> packagesInfo;

    /**
     * Packages for which there is no unit tester class.
     */
    private List<String> nonUnitTestedPackageNames = new LinkedList<String>();

    private DoPackageWork worker;

    /** Prints out the version. */
    static {
        System.err.println( VERSION );
    }

    /**
     * Create and run a <code>GrandTestAuto</code> that will take its
     * settings from the named file.
     *
     * @param args a single string only, specifying the name of the settings file.
     */
    public static void main( String[] args ) {
        if (args.length == 1) {
            try {
                //Check that it's actually a file.
                File f = new File( args[0] );
                if (!f.exists()) {
                    writeCorrectedSettings( f, new Settings() );
                    p( Messages.message( Messages.OPK_SETTINGS_FILE_NOT_FOUND_SO_WRITTEN, args[0] ) );
                    return;
                }
                Settings settings = new Settings( args[0] );
                if (!settings.unknownKeys().isEmpty()) {
                    File correctedFile = correctedFileFor( f );
                    writeCorrectedSettings( correctedFile, settings );
                    p( Messages.message( Messages.OPK_SETTINGS_FILE_HAS_PROBLEMS, args[0] ) );
                    p( Messages.message( Messages.OPK_CORRECTED_SETTINGS_FILE_WRITTEN, correctedFile.getAbsolutePath() ) );
                    p( Messages.message( Messages.SK_GTA_CONTINUING_WITH_SETTINGS_THAT_COULD_BE_READ ) );
                }
                GrandTestAuto gta = new GrandTestAuto( settings );
                gta.runAllTests();
            } catch (Exception e) {
                p( Messages.message( Messages.SK_GTA_COULD_NOT_RUN ) );
                e.printStackTrace();
            }
        } else if (args.length == 2) {
            if (!args[0].equals( "-run" )) {
                p( Messages.message( Messages.SK_GTA_NEEDS_TWO_ARGUMENTS ) );
            }
            try {
                runAutoLoadTest( args[1], args[1], null );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            p( Messages.message( Messages.SK_GTA_NEEDS_TWO_ARGUMENTS ) );
        }
    }

    private static void writeCorrectedSettings( File correctedFile, Settings settings ) throws IOException {
        BufferedWriter bw = new BufferedWriter( new FileWriter( correctedFile ) );
        bw.write( settings.commentedPropertiesWithTheseValues() );
        bw.close();
    }

    private static File correctedFileFor( File file ) {
        File dir = file.getParentFile();
        String relName = file.getName();
        File corrected = new File( dir, "Corrected_" + relName );
        int i = 1;
        while (corrected.exists()) {
            corrected = new File( dir, "Corrected_" + i + "_" + relName );
            i++;
        }
        return corrected;
    }

    /**
     * Creates a <code>GrandTestAuto</code> using the given settings.
     *
     * @see Settings
     */
    public GrandTestAuto( final Settings settings ) throws IOException {
        this( settings, null );
    }

    GrandTestAuto( final Settings settings, DoPackageWork dpw ) throws IOException {
        this.settings = settings;
        resultsLogger = new ResultsLogger( settings.resultsFileName(), settings.logToConsole() );
        packagesInfo = new PackagesInfo<PackageInfo>( new PackagesInfo.Filter() {
            public boolean accept( String packageName ) {
                return GrandTestAuto.this.settings.packageNameFilter().accept( packageName ) && PackagesInfo.namesPackageThatMightNeedUnitTests( packageName );
            }
        }, settings.classesDir() ) {
            public PackageInfo createClassFinder( String packageName, File baseDir ) {
                return new PackageInfo( packageName, baseDir );
            }
        };
        if (dpw == null) {
            DPWImpl dpwImpl = new RunsTestsInPackages();
            dpwImpl.setGTA( this );
            this.worker = dpwImpl;
        } else {
            worker = dpw;
        }
    }

    /**
     * Runs the <code>UnitTester</code> classes in all
     * packages in the classes directory, then the function tests,
     * then the load tests. Prints result details
     * to a results file (defined in the <code>Settings</code>)
     * and also returns a <code>boolean</code> as a summary of all tests.
     *
     * @return <code>true</code> if all tests passed (which also
     *         implies no exceptions were thrown).
     */
    public boolean runAllTests() {
        if (worker.verbose() && !settings.lessVerboseLogging()) {
            resultsLogger.log( Messages.message( Messages.SK_ABOUT_TO_RUN_TESTS ), null );
            resultsLogger.log( settings.summary(), null );
        }
        boolean result = true;
        if (settings.runUnitTests()) {
            result = runUnitTests();
        }
        if (settings.runFunctionTests()) {
            if (continueWithTests( result )) {
                result &= runAutoLoadTests( true );
            } else {
                resultsLogger.log( Messages.message( Messages.SK_FAIL_FAST_SKIP_FUNCTION_TESTS ), null );
            }
        }
        if (settings.runLoadTests()) {
            if (continueWithTests( result )) {
                result &= runAutoLoadTests( false );
            } else {
                resultsLogger.log( Messages.message( Messages.SK_FAIL_FAST_SKIP_LOAD_TESTS ), null );
            }
        }
        //Record overall results.
        reportOverallResult( result, Messages.OPK_OVERALL_GTA_RESULT );
        resultsLogger.closeLogger();
        return result;
    }

    public ResultsLogger resultsLogger() {
        return resultsLogger;
    }

    private boolean runAutoLoadTests( final boolean areFunctionTests ) {
        String key;
        if (worker.verbose() && !settings.lessVerboseLogging()) {
            key = areFunctionTests ? Messages.SK_RUNNING_FUNCTION_TESTS : Messages.SK_RUNNING_LOAD_TESTS;
            resultsLogger.log( Messages.message( key ), null );
        }
        boolean result = true;
        PackagesInfo<AutoLoadTestFinder> testPackageFinder = createAutoLoadTestPackagesInfo( areFunctionTests );
        for (String testPackageName : testPackageFinder.testablePackageNames()) {
            //Shortcut these tests if a test has failed.
            if (!continueWithTests( result )) {
                key = areFunctionTests ? Messages.SK_FAIL_FAST_FUNCTION_TESTS : Messages.SK_FAIL_FAST_LOAD_TESTS;
                resultsLogger.log( Messages.message( key ), null );
                break;
            }
            boolean packageResult = worker.runAutoLoadTestPackage( areFunctionTests, testPackageFinder, testPackageName );
            //Report the result for this package.
            String pf = Messages.passOrFail( packageResult );
            key = areFunctionTests ? Messages.TPK_FUNCTION_TEST_PACK_RESULTS : Messages.TPK_LOAD_TEST_PACK_RESULTS;
            resultsLogger.log( Messages.message( key, testPackageName, pf ), null );
            result &= packageResult;
        }
        //Report the overall result, if applicable. @todo write/change tests for this.
        //
        if (areFunctionTests && settings.runFunctionTests() || !areFunctionTests && settings.runLoadTests()) {
            key = areFunctionTests ? Messages.OPK_OVERALL_FUNCTION_TEST_RESULT : Messages.OPK_OVERALL_LOAD_TEST_RESULT;
            reportOverallResult( result, key );
        }
        return result;
    }

    private PackagesInfo<AutoLoadTestFinder> createAutoLoadTestPackagesInfo( final boolean areFunctionTests ) {
        return new PackagesInfo<AutoLoadTestFinder>( new PackagesInfo.Filter() {
            public boolean accept( String packageName ) {
                return settings.packageNameFilter().accept( packageName ) && (areFunctionTests ? PackagesInfo.namesFunctionTestPackage( packageName ) : PackagesInfo.namesLoadTestPackage( packageName ));
            }
        }, settings.classesDir() ) {
            public AutoLoadTestFinder createClassFinder( String packageName, File baseDir ) {
                return new AutoLoadTestFinder( packageName, baseDir );
            }
        };
    }

    boolean classIsToBeSkippedBecauseOfSettings( String className ) {
        return settings.classNameFilter() != null && !settings.classNameFilter().accept( className );
    }

    static boolean runAutoLoadTest( String fullClassName, String testName, ResultsLogger resultsLogger ) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class klass = Class.forName( fullClassName );
        AutoLoadTest ft = (AutoLoadTest) klass.newInstance();
        //Report the result.
        boolean resultForTest = false;
        long timeJustBeforeTestRun = System.currentTimeMillis();
        try {
            resultForTest = ft.runTest();
        } catch (Throwable e) {
            String msg = Messages.message( Messages.OPK_ERROR_RUNNING_AUTO_LOAD_TEST, testName );
            if (resultsLogger != null) {
                resultsLogger.log( msg, e );
            } else {
                e.printStackTrace();
                System.out.println( msg );
            }
        }
        long timeJustAfterTestRun = System.currentTimeMillis();
        long timeToRunTest = timeJustAfterTestRun - timeJustBeforeTestRun;
        String msg = testName + " " + Messages.passOrFail( resultForTest ) + ", " + formatTestExecutionTime( timeToRunTest );
        if (resultsLogger != null) {
            resultsLogger.log( msg, null );
        } else {
            System.out.println( msg );
        }
        return resultForTest;
    }

    private static String formatTestExecutionTime( long timeInMillis ) {
        float timeInSeconds = ((float) timeInMillis) / 1000f;
        NumberFormat testTimeFormatter = NumberFormat.getNumberInstance();
        return testTimeFormatter.format( timeInSeconds ) + "s";
    }

    private boolean runUnitTests() {
        if (worker.verbose() && !settings.lessVerboseLogging()) {
            resultsLogger.log( Messages.message( Messages.SK_RUNNING_UNIT_TESTS ), null );
        }
        List<String> packageNames = findUnitTestPackages();
        boolean result = true;
        for (String packageName : packageNames) {
            //Shortcut these tests if a test has failed.
            if (!continueWithTests( result )) {
                resultsLogger.log( Messages.message( Messages.SK_FAIL_FAST_UNIT_TESTS ), null );
                break;
            }
            //Ignore the test packages themselves.
            if (!packageName.endsWith( ".test" )) {
                result &= worker.doUnitTests( packageName );
            }
        }
        reportUntestedPackages();
        reportOverallResult( result, Messages.OPK_OVERALL_UNIT_TEST_RESULT );
        return result;
    }

    private List<String> findUnitTestPackages() {
        List<String> packageNames = new LinkedList<String>();
        for (String packageName : packagesInfo.testablePackageNames()) {
            //Ignore the test packages themselves.
            if (!packageName.endsWith( ".test" )) {
                packageNames.add( packageName );
            }
        }
        return packageNames;
    }

    /**
     * Information about the packages in the project.
     */
    PackagesInfo<PackageInfo> packagesInfo() {
        return packagesInfo;
    }

    /**
     * The parameters for the running of the tests.
     */
    Settings settings() {
        return settings;
    }

    private static void p( String msg ) {
        System.out.println( msg );
    }

    /**
     * This method is for handling errors in the setup of the tests
     * that compromise the running of the tests. For example, if a
     * <code>UnitTester</code> could not be run.
     */
    void testingError( String message, Throwable t ) {
        resultsLogger.log( message, t );
    }

    /**
     * Report the given result for the unit tester of the named package.
     */
    void reportUTResult( String packName, boolean result ) {
        String pf = Messages.passOrFail( result );
        resultsLogger.log( Messages.message( Messages.TPK_UNIT_TEST_PACK_RESULTS, packName, pf ), null );
    }

    private void reportUntestedPackages() {
        if (!nonUnitTestedPackageNames.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            buff.append( Messages.message( Messages.CK_PACKAGE_NOT_UNIT_TESTED, nonUnitTestedPackageNames.size() ) );

            for (String nonUnitTestedPackageName : nonUnitTestedPackageNames) {
                buff.append( Messages.nl() );
                buff.append( nonUnitTestedPackageName );
            }
            resultsLogger.log( buff.toString(), null );
        }
    }

    private void reportOverallResult( boolean result, String key ) {
        if (!worker.verbose()) return;
        //If there's only one package, don't report results.
        if (settings.packageNameFilter().allowsASinglePackageOnly()) return;
        String pf = Messages.passOrFail( result );
        resultsLogger.log( Messages.message( key, pf ), null );
    }

    boolean continueWithTests( boolean resultSoFar ) {
        return !settings.stopAtFirstFailure() || resultSoFar;
    }

    List<String> nonUnitTestedPackageNames() {
        return nonUnitTestedPackageNames;
    }
}