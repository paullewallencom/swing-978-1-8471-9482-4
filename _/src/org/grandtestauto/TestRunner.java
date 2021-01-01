/****************************************************************************
 *
 * Name: TestRunner.java
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * For running the test methods in a particular test class.
 *
 * @author Tim Lavers
 */
public class TestRunner {

    /**
     * The class that is the test.
     */
    private Class<?> testClass;

    /**
     * The methods in the test class that are to be run.
     */
    private SortedSet<Method> methodsToRun;

    /**
     * If not null, used to filter out some tests.
     */
    private NameFilter testMethodNameFilter;

    private String behaviourOnTestFailure = System.getProperty( "gta.behaviourOnTestFailure" );

    /**
     * Run all or some of the test methods in the class named by the first argument
     * (arguments after the first name the test methods to be run; if there is only
     * one argument, all are run). The methods are run in alphabetical order of their names.
     * The test methods are those that are public, return a boolean,
     * take no arguments and have name ending in "Test". The test
     * class must have a public no-args constructor.
     */
    public static void main( String[] args ) {
        try {
            SortedSet<String> methodNames = new TreeSet<String>();
            for (int i = 1; i < args.length; i++) {
                methodNames.add( args[i] );
            }
            TestRunner tr = new TestRunner( args[0], methodNames );
            boolean b = tr.runTestMethods( null, null );
            System.out.println( "Result: " + Messages.passOrFail( b ) );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println( Messages.message( Messages.SK_COULD_NOT_RUN_TESTS ) );
        }
    }

    TestRunner( String testClassName, SortedSet<String> methodNames ) throws ClassNotFoundException {
        this( Class.forName( testClassName ), methodNames, null );
    }

    TestRunner( Class<?> testClass, SortedSet<String> methodNames, NameFilter testMethodNameFilter ) {
        this.testClass = testClass;
        this.testMethodNameFilter = testMethodNameFilter;
        //Compare methodsToRun based on their name alone, as no two
        //test methods can have the same name.
        methodsToRun = new TreeSet<Method>( new Comparator<Method>() {
            public int compare( Method m1, Method m2 ) {
                return m1.getName().compareTo( m2.getName() );
            }
        } );
        if (methodNames == null || methodNames.isEmpty()) {
            //Get all test methods.
            Method[] allDeclared = testClass.getDeclaredMethods();
            for (Method declared : allDeclared) {
                if (isTestMethod( declared )) {
                    if (testMethodNameFilter == null || testMethodNameFilter.accept( declared.getName() )) {
                        methodsToRun.add( declared );
                    }
                }
            }
        } else {
            //Just use the named test methods.
            for (String methodName : methodNames) {
                try {
                    Method m = testClass.getDeclaredMethod( methodName, new Class<?>[0] );
                    if (isTestMethod( m )) {
                        methodsToRun.add( m );
                    } else {
                        System.err.println( Messages.message( Messages.OPK_NOT_A_TEST_METHOD, methodName ) );
                    }
                } catch (NoSuchMethodException e) {
                    System.err.println( Messages.message( Messages.OPK_COULD_NOT_FIND_TEST_METHOD, methodName ) );
                }
            }
        }
    }

    /**
     * @param analyser may be null
     * @throw <code>InstantiationException</code> if the test class does
     * not have a no-args constructor or is not concrete.
     * @throw <code>IllegalAccessException</code> if the test class
     * or its no-args constructor are not public.
     * @throw <code>InvocationTargetException</code> if a test method
     * throws an exception when invoked.
     */
    boolean runTestMethods( CoverageUnitTester cut, ClassAnalyser analyser ) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        boolean result = true;
        //Object to call the test methods on.
        Object testObj = testClass.newInstance();
        //Empty params array for invoking test methods.
        for (Method testMethod : methodsToRun) {
            //Check that the method returns a boolean.
            if (!testMethod.getReturnType().equals( boolean.class )) {
                String msg = Messages.message( Messages.OPK_TEST_METHOD_DOES_NOT_RETURN_BOOLEAN, testMethod.getName() );
                if (cut != null) {
                    cut.resultsLogger().log( msg, null );
                }
                result = false;
                //Record m as untested.
                if (cut != null) {
                    cut.accountant().noTestFound( testMethod );
                }
                //Don't try to run it.
                continue;
            }
            try {
                //Run the test method.
                Boolean b = (Boolean) testMethod.invoke( testObj );
                result &= b;
                //Record the method as tested.
                if (analyser != null) {
                    analyser.recordTestDone( testMethod.getName(), cut.accountant() );
                }
                if (cut != null) {
                    cut.reportResult( testMethod, b );
                } else {
                    reportResult( testMethod, b );
                }
            } catch (IllegalAccessException iae) {
                //This is an error in the Unit test.
                if (cut != null) {
                    cut.testingError( Messages.message( Messages.OPK_COULD_NOT_RUN_TEST_METHOD, testMethod.getName() ), iae );
                }
                result = false;
            } catch (InvocationTargetException ita) {
                //The test threw an exception. Print out the underlying exception (it might contain valuable information)
                //and count this as the test failing.
                ita.getCause().printStackTrace();
                result = false;
                if (cut != null) {
                    cut.reportResult( testMethod, false );
                } else {
                    reportResult( testMethod, false );
                }
                if ("exit".equalsIgnoreCase( behaviourOnTestFailure )) {
                    System.out.println( "Exiting as test failed." );
                    System.exit( 0 );
                } else if ("halt".equalsIgnoreCase( behaviourOnTestFailure )) {
                    System.out.println( "Pausing indefinitely as test failed." );
                    try {
                        Thread.sleep( Long.MAX_VALUE );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Record that there is a test for the method (even if it failed).
            if (cut != null) {
                cut.accountant().testFound( testMethod );
            }
        }
        return result;
    }

    private void reportResult( Method m, boolean b ) {
        StringBuffer buff = new StringBuffer();
        buff.append( Messages.passOrFail( b ) );
        buff.append( ' ' );
        buff.append( m.getName() );
        System.out.println( buff.toString() );
    }

    private boolean isTestMethod( Method m ) {
        boolean isTesty = m.getName().endsWith( "Test" );
        isTesty &= Modifier.isPublic( m.getModifiers() );
        isTesty &= !Modifier.isAbstract( m.getModifiers() );
        isTesty &= m.getReturnType().equals( boolean.class );
        isTesty &= m.getParameterTypes().length == 0;
        return isTesty;
    }
}