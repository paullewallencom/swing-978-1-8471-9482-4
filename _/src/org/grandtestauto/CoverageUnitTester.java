/****************************************************************************
 *
 * Name: CoverageUnitTester.java
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
import java.lang.reflect.*;
import java.util.*;

/**
 * A <code>CoverageUnitTester</code> runs the unit tests
 * for all of the concrete accessible classes in its parent package.
 * It checks that each testable class <code>X</code> has a unit test
 * called <code>XTest</code>,
 * and that each method of a class is tested, including
 * inherited methods.
 * <p/>
 * For example, consider a package containing three
 * public classes <code>A</code>, <code>B</code> and <code>C</code>.
 * Suppose that <code>A</code> is an abstract class
 * with testable methods <code>a()</code>, <code>b()</code>, <code>c()</code>.
 * Suppose that <code>B</code> and <code>C</code> both extend
 * <code>A</code> and that <code>B</code> re-defines <code>b()</code>
 * and <code>C</code> re-defines <code>c()</code> as well as
 * defining a new method <code>e()</code>.
 * To ensure coverage, the test classes for this hierarchy
 * must be called <code>BTest</code> and <code>CTest</code>.
 * <code>BTest</code> must contain a test for <code>b()</code>.
 * <code>CTest</code> must contain tests for <code>c()</code>
 * and <code>e()</code>. Either <code>BTest</code>
 * or <code>CTest</code> may contain a test for <code>a()</code>,
 * but one of these classes must contain such a test.
 *
 * @author Tim Lavers
 */
public class CoverageUnitTester implements UnitTesterIF {

    /**
     * Controls the tests. Will be null if main is used.
     */
    private GrandTestAuto gta;

    /**
     * The name of the package that this tests.
     */
    private String packageTested;

    /**
     * Untested classes.
     */
    private Set<String> untestedClasses = new HashSet<String>();

    /**
     * Records which methods are tested.
     */
    private Accountant accountant = new Accountant();

    /**
     * For recording results.
     */
    private ResultsLogger resultsLogger;

    /**
     * Details of classes to be tested.
     */
    private PackageInfo pi;

    /**
     * Finds all test classes, so that ones that don't correspond to a testable class can be run.
     */
    private TestFinder testFinder;

    /**
     * Classes that have already been printed out in the results.
     */
    private Set<Class> classesAlreadyPrinted = new HashSet<Class>();

    /**
     * Creates a <code>CoverageUnitTester</code> that will run unit tests
     * for classes in the parent package to the package in which this
     * is defined.
     */
    public CoverageUnitTester( GrandTestAuto gta ) {
        this.gta = gta;
        //Set the packageTested to be the name of this package minus the string ".test".
        String packName = getClass().getPackage().getName();
        packageTested = packName.substring( 0, packName.length() - 5 );
        resultsLogger = gta.resultsLogger();
        pi = gta.packagesInfo().packageInfo( packageTested );
        if (pi != null) {
            pi.seek();
        }
        File classesDir = new File( gta.packagesInfo().classesRoot(), packageTested.replace( '.', File.separatorChar ) );
        classesDir = new File( classesDir, "test" );
        testFinder = new TestFinder( packName, classesDir );
        testFinder.seek();
    }

    public CoverageUnitTester( String classesRoot ) {
        this.gta = null;
        //Set the packageTested to be the name of this package minus the string ".test".
        String packName = getClass().getPackage().getName();
        //Strip off ".test"
        packageTested = packName.substring( 0, packName.length() - 5 );
        //Create the logger.
        try {
            resultsLogger = new ResultsLogger( null, true );
        } catch (IOException e) {
            //Don't want to throw this as it will make subclassing more difficult (and in particular
            //invalidate a lot of test subclasses). So assert fail.
            e.printStackTrace();
            assert false;
        }
        //Create the PackageInfo.
        //The subdir of classesRoot containing the classes to be tested.
        String relativeDir = packageTested.replace( '.', File.separatorChar );
        File dir = new File( classesRoot, relativeDir );
        pi = new PackageInfo( packageTested, dir );
        pi.seek();
        File testClassesDir = new File( dir, "test" );
        testFinder = new TestFinder( packName, testClassesDir );
        testFinder.seek();
    }

    /**
     * Runs the unit tests for all of the accessible concrete classes
     * in the package being tested. Records classes and methods not tested.
     */
    public boolean runTests() {
        boolean result = true;
        if (pi == null) {
            //There was nothing to test.
            return true;
        }
        String testPackageName = packageTested + ".test.";
        //Run the minimal set of tests: those corresponding to public concrete classes in the package under test.
        //Record the names of the actual test classes that are run, so that we don't run them again later.
        Set<String> namesOfTestClassesRunSoFar = new HashSet<String>();
        for (String className : pi.classNameToTestability().keySet()) {
            //Curtail tests if we've already failed and failfast is on.
            if (gta != null && !gta.continueWithTests( result )) break;

            //Skip this test if we're only running selected tests in a package.
            if (gta != null && gta.classIsToBeSkippedBecauseOfSettings( className )) continue;

            String fullName = packageTested + '.' + className;
            try {
                Class toTest = Class.forName( fullName );
                //Add all method and constructor tests to the accountant as missing.
                //These will be accounted for as the tests are run.
                ClassAnalyser analyser = new ClassAnalyser( toTest );
                for (Method testableMethod : analyser.testableMethods()) {
                    accountant.noTestFound( testableMethod );
                }
                for (Constructor constructor : analyser.testableConstructors()) {
                    accountant.recordAsNeedingTest( constructor );
                }
                String testClassName = testPackageName + className + "Test";
                namesOfTestClassesRunSoFar.add( testClassName );
                result = runTests( testClassName, className, analyser, pi.classNameToTestability().get( className ) );
            } catch (Exception e) {
                //Most unlikely as the class has already been instantiated once.
                testingError( Messages.message( Messages.SK_BUG_IN_GTA ), e );
            }
        }
        //Now run the extra tests, if there are any.
        for (String className : testFinder.classesInPackage()) {
            //Curtail tests if we've already failed and failfast is on.
            if (gta != null && !gta.continueWithTests( result )) break;

            //Skip this test if we're only running selected tests in a package.
            if (gta != null && gta.classIsToBeSkippedBecauseOfSettings( className )) continue;

            try {
                String testClassName = testPackageName + className;
                if (!namesOfTestClassesRunSoFar.contains( testClassName )) {
                    result = runTests( testClassName, null, null, Testability.NO_TEST_REQUIRED );
                }
            } catch (Exception e) {
                //Most unlikely as the class has already been instantiated once.
                testingError( Messages.message( Messages.SK_BUG_IN_GTA ), e );
            }
        }

        if (!accountant.untestedMethods().isEmpty()) {
            result = false;
        }
        if (!accountant.untestedConstructors().isEmpty()) {
            result = false;
        }
        reportUntested();
        return result;
    }

    private boolean runTests( String testClassName, String nameOfClassUnderTestOrNull, ClassAnalyser analyser, Testability testability ) throws InvocationTargetException {
        boolean result = true;
        try {
            Class testClass = Class.forName( testClassName );
            NameFilter testMethodNameFilter = null;
            if (gta != null) {
                testMethodNameFilter = gta.settings().methodNameFilter();
            }
            TestRunner runner = new TestRunner( testClass, null, testMethodNameFilter );
            //The TestRunner runs the tests and the accountant ticks them off.
            result &= runner.runTestMethods( this, analyser );
        } catch (ClassNotFoundException cnfe) {
            if (testability.equals( Testability.TEST_REQUIRED )) {
                //There is no test class defined. Record this and cause failure.
                untestedClasses.add( nameOfClassUnderTestOrNull );
                result = false;
            }
        } catch (IllegalAccessException iae) {
            //The test class is badly written.
            testingError( Messages.message( Messages.OPK_COULD_NOT_CREATE_TEST_CLASS, testClassName ), iae );
            result = false;
        } catch (InstantiationException ie) {
            //The test class is badly written.
            testingError( Messages.message( Messages.OPK_COULD_NOT_CREATE_TEST_CLASS, testClassName ), ie );
            result = false;
        }
        return result;
    }

    Accountant accountant() {
        return accountant;
    }

    ResultsLogger resultsLogger() {
        return resultsLogger;
    }

    void testingError( String str, Exception e ) {
        if (gta != null) {
            gta.testingError( str, e );
        } else {
            System.err.println( str );
            e.printStackTrace();
        }
    }

    private void reportUntested() {
        //Are there any untested classes to report?
        reportUntested( untestedClasses, Messages.TPK_CK_CLASSES_NOT_TESTED, new ItemFormatter() {
            public String format( Object o ) {
                return o.toString();
            }
        } );
        //Are there any untested constructors?
        reportUntested( accountant.untestedConstructors(), Messages.CK_CONSTRUCTOR_TESTS_EXPECTED, new ItemFormatter() {
            public String format( Object o ) {
                return o.toString();
            }
        } );
        //Are there any untested methods?
        reportUntested( accountant.untestedMethods(), Messages.TPK_CK_METHODS_NOT_TESTED, new ItemFormatter() {
            public String format( Object o ) {
                return o.toString();
            }
        } );
    }

    /**
     * Reports the results of running the given test method.
     */
    void reportResult( Method testMethod, boolean result ) {
        StringBuffer message = new StringBuffer();
        Class<?> declaringClass = testMethod.getDeclaringClass();
        if (!classesAlreadyPrinted.contains( declaringClass )) {
            String nameToLog = declaringClass.getName().substring( packageTested.length() + 6 );
            resultsLogger.log( nameToLog, null );
            classesAlreadyPrinted.add( declaringClass );
        }
        message.append( "    " );
        message.append( testMethod.getName() );
        message.append( " " );
        message.append( Messages.passOrFail( result ) );
        resultsLogger.log( message.toString(), null );
    }

    private interface ItemFormatter {
        String format( Object o );
    }

    private void reportUntested( Set untested, String key, ItemFormatter formatter ) {
        if (!untested.isEmpty()) {
            StringBuffer msg = new StringBuffer();
            msg.append( Messages.message( key, packageTested, untested.size() ) );
            msg.append( Messages.nl() );
            for (Iterator itor = untested.iterator(); itor.hasNext();) {
                msg.append( formatter.format( itor.next() ) );
                if (itor.hasNext()) {
                    msg.append( Messages.nl() );
                }
            }
            msg.append( "." );
            resultsLogger.log( msg.toString(), null );
        }
    }
}