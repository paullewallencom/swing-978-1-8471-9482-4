/****************************************************************************
 *
 * Name: ClassAnalyser.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A <code>ClassAnalyser</code> is used (by <code>CoverageUnitTester</code>)
 * to obtain the testable methods of a (non-abstract) class. These are the
 * accessible methods that are declared 'within the package' of the class.
 * (That is, by an ancestor of the class within the same package.)
 * <code>ClassAnalyser</code> also provides the list of names of constructor
 * test methods that a class should have.
 * <p/>
 * As well as calculating which methods are to be tested, a <code>ClassAnalyser</code>
 * works out the name of the test for a method.
 * All unit test methods invoked by <code>CoverageUnitTester</code>
 * are to be public, have no arguments, and return a boolean.
 * So if there are multiple methods in a class that are distinguished by their
 * parameters, the corresponding test methods must be distinguished by name alone.
 * The naming pattern used for test methods is:
 * <ul>
 * <li>suppose that method <b>meth</b> has name <i>m</i> and that no other
 * testable method in the class has this name. Then the name of the test method
 * for <b>meth</b> is <i>mTest</i>.</li>
 * <li>Suppose that there are several methods <b>meth1</b>, <b>meth2</b>,.... with
 * the name <i>m</i>. Then the test methods for these have names that take
 * into account the types of the parameters for the methods. These compound names
 * are calculated as follows:
 * <ul>
 * <li>the first part of the name is <i>m</i></li>
 * <li>for each parameter type, the string '_TYPE' is appended, where
 * <ul>
 * <li>for object types, 'TYPE' is the unqualified class name of the type</li>
 * <li>for primitive types, 'TYPE' is the name of the type</li>
 * <li>for arrays of object type, 'TYPE' is the unqualified name of the type,
 * followed by "Array", followed by the empty string for arrays
 * of dimension 1 or by the dimension of the array for higher
 * dimensional arrays.</li>
 * <li>for arrays of primitive type, the same rules as for
 * object-type arrays, but with the type of the primitive.</li>
 * </ul></li>
 * (By unqualified class name is meant the name of the class stripped of
 * its package name.)
 * <li>the last part of the name is <i>_Test</i></li>.
 * </ul></li>
 * </ul>
 * As an example of this naming pattern, suppose that class <code>X</code> includes
 * the testable methods <code>m()</code>, <code>m( String )</code>, <code>m( int )</code> and
 * <code>m( String[], int[][] )</code>. Then the names of the corresponding test
 * methods would be <code>m_Test()</code>, <code>m_String_Test()</code>,
 * <code>m_int_Test()</code>, <code>m_StringArray_intArray2_Test()</code>.
 *
 * @author Tim Lavers
 */
public class ClassAnalyser {

    /**
     * The first part of method names for constructors.
     */
    public static final String CONSTRUCTOR = "constructor";

    /**
     * The class to be analysed.
     */
    private Class klass;

    /**
     * Collects the testable methods by the names that their tests should have.
     */
    private Map<String, Method> testNameToMethod = new HashMap<String, Method>();

    /**
     * Collects the testable constructors by the names that their tests should have.
     */
    private Map<String, Constructor> testNameToConstructor = new HashMap<String, Constructor>();

    /**
     * The representation of the given class as used in compound test names.
     */
    public static String nameInTestMethodNames( Class c ) {
        String result;
        if (c.isPrimitive()) {
            result = c.getName();
        } else if (c.isArray()) {
            ArrayArgNameConverter aarg = new ArrayArgNameConverter( c.getName() );
            result = aarg.name();
        } else {
            String packName = c.getPackage().getName();
            result = c.getName().substring( packName.length() + 1 );
        }
        return result;
    }

    /**
     * Creates a <code>ClassAnalyser</code> for the given <code>Class</code>.
     */
    public ClassAnalyser( Class clazz ) {
        klass = clazz;
        //Get all methods from this class and its superclasses and populate the map of testName to Method.
        LinkedList<Method> methodsToTest = new LinkedList<Method>();
        addMethodsFrom( klass, methodsToTest );
        //At this point, methodsToTest is a list of all testable methods
        //from the class and its superclasses. The order of the elements
        //is: methods from clazz, methods from clazz.super, methods from
        //clazz.super.super etc.
        for (Method methodToBeTested : methodsToTest) {
            String testMethodName = testMethodName( methodToBeTested, methodsToTest );
            //It might be that clazz over-rides a testable method.
            //We don't want the over-ridden method to displace the one
            //from clazz. (Another test should test it.)
            if (!testNameToMethod.containsKey( testMethodName )) {
                testNameToMethod.put( testMethodName, methodToBeTested );
            }
        }
        //Get the set of testable constructors and populate the map of test name to constructor.
        Set<Constructor> testable = new HashSet<Constructor>();
        //Get all of the constructors, then weed out inaccessible ones.
        if (!Modifier.isAbstract( clazz.getModifiers())) {
            Constructor[] constructors = klass.getDeclaredConstructors();
            for (int i = 0; i < constructors.length; i++) {
                int m = constructors[i].getModifiers();
                if (Modifier.isPublic( m ) || Modifier.isProtected( m )) {
                    testable.add( constructors[i] );
                }
            }
        }
        for (Constructor constructor : testable) {
            testNameToConstructor.put( getTestNameFor( constructor, testable.size() > 1 ), constructor );
        }
    }

    public boolean isTestable( Method meth ) {
        //Synthetic methods need not be tested.
        if (meth.isSynthetic()) return false;

        //Methods constructed by the compiler, in enum classes, do not need testing.
        if (klass.isEnum()) {
            //The arg-less values() method.
            if (meth.getName().equals( "values" ) && meth.getParameterTypes().length == 0) return false;
            //The valueOf method (single String arg).
            if (meth.getName().equals( "valueOf" )) {
                Class[] parameterTypes = meth.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0].equals( String.class )) {
                    return false;
                }
            }
        }
        int m = meth.getModifiers();
        boolean result = false;
        if (!Modifier.isAbstract( m )) {
            if (Modifier.isPublic( m )) {
                result = true;
            } else if (Modifier.isProtected( m )) {
                if (!Modifier.isFinal( m ) && !Modifier.isFinal( klass.getModifiers() )) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * The analysed class.
     */
    public Class analysedClass() {
        return klass;
    }

    /**
     * The names of the constructor and method test methods that should be declared for the analysed class.
     */
    public SortedSet<String> testMethodNames() {
        SortedSet<String> result = new TreeSet<String>();
        result.addAll( testNameToConstructor.keySet() );
        result.addAll( testNameToMethod.keySet() );
        return result;
    }

    /**
     * The testable constructors of the class.
     */
    public Collection<Constructor> testableConstructors() {
        return testNameToConstructor.values();
    }

    /**
     * The testable methods of the class.
     */
    public Collection<Method> testableMethods() {
        return testNameToMethod.values();
    }

    void recordTestDone( String testMethodName, Accountant accountant ) {
        if (testNameToConstructor.keySet().contains( testMethodName )) {
            accountant.recordAsTested( testNameToConstructor.get( testMethodName ) );
        } else if (testNameToMethod.keySet().contains( testMethodName )) {
            accountant.testFound( testNameToMethod.get( testMethodName ) );
        }
    }

    private void addMethodsFrom( Class c, List<Method> methodsToTest ) {
        //Get the methods from c.
        Method[] dec = c.getDeclaredMethods();
        for (Method aDec : dec) {
            if (isTestable( aDec )) {
                methodsToTest.add( aDec );
            }
        }
        //And recursively get the superclass methods.
        Class superc = c.getSuperclass();
        if (superc == null) return;
        if (superc.getPackage().equals( c.getPackage() )) {
            addMethodsFrom( superc, methodsToTest );
        }
    }

    private static String mangledNames( Class[] argClasses ) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < argClasses.length; i++) {
            if (i == 0) {
                buff.append( '_' );
            }
            buff.append( nameInTestMethodNames( argClasses[i] ) );
            buff.append( '_' );
        }
        return buff.toString();
    }

    /**
     * Whether or not there is a need to use the compound test method name for the given method.
     */
    private static boolean needsCompoundName( Method m, Collection<Method> methodsToBeTested ) {
        boolean result = false;
        int nameRepetition = 0;
        for (Method nextMeth : methodsToBeTested) {
            //Skip over the method itself
            if (nextMeth.equals( m)) continue;
            if (nextMeth.getName().equals( m.getName() )) {
                //Check to see if it is a method re-definition.
                Class[] methParams = m.getParameterTypes();
                Class[] nextMethParams = nextMeth.getParameterTypes();
                //Skip over a re-definition.
                if (Arrays.equals( methParams,  nextMethParams )) continue;
                nameRepetition++;
                if (nameRepetition > 0) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * The name of the test method that should be defined for the given constructor,
     * taking into account whether or not compound names are needed, as indicated
     * by the flag.
     *
     * @param c the constructor in question.
     * @param compoundNameNeeded
     *                            is a compound name needed
     *                            (because there is more than one constructor to be tested).
     */
    private String getTestNameFor( Constructor c, boolean compoundNameNeeded ) {
        StringBuffer buff = new StringBuffer();
        buff.append( CONSTRUCTOR );
        if (compoundNameNeeded) {
            Class[] argTypes = c.getParameterTypes();
            buff.append( mangledNames( argTypes ) );
        }
        buff.append( "Test" );
        return buff.toString();
    }

    /**
     * Name of the test method that should be defined for the given method.
     * The class documentation explains the way this method works.
     */
    private static String testMethodName( Method m, LinkedList<Method> methodsToBeTested ) {
        String result;
        if (needsCompoundName( m, methodsToBeTested )) {
            StringBuffer buff = new StringBuffer();
            buff.append( m.getName() );
            Class[] argClasses = m.getParameterTypes();
            buff.append( mangledNames( argClasses ) );
            buff.append( "Test" );
            result = buff.toString();
        } else {
            result = m.getName() + "Test";
        }
        return result;
    }

    /**
     * Used to convert array class names to strings representing them in test method names.
     */
    private static class ArrayArgNameConverter {
        private String typeName;
        private int dimension;

        ArrayArgNameConverter( String arrayClassName ) {
            //Get the dimension by the number of '['s.
            int posLast = arrayClassName.lastIndexOf( '[' );
            dimension = posLast + 1;
            String type = arrayClassName.substring( posLast + 1 );
            //If the type is one char long, it represents a primitive.
            if (type.length() == 1) {
                typeName = primitiveTypeName( type.charAt( 0 ) );
            } else {
                int lastDot = type.lastIndexOf( "." );
                typeName = type.substring( lastDot + 1, type.length() - 1 );
            }
        }

        String name() {
            StringBuffer result = new StringBuffer();
            result.append( typeName );
            result.append( "Array" );
            if (dimension > 1) {
                result.append( dimension );
            }
            return result.toString();
        }

        private static String primitiveTypeName( char c ) {
            String result;
            switch (c) {
                case 'Z':
                    result = "boolean";
                    break;
                case 'B':
                    result = "byte";
                    break;
                case 'C':
                    result = "char";
                    break;
                case 'S':
                    result = "short";
                    break;
                case 'I':
                    result = "int";
                    break;
                case 'F':
                    result = "float";
                    break;
                case 'J':
                    result = "long";
                    break;
                case 'D':
                    result = "double";
                    break;
                default:
                    //assert false : "Unexpected primitive flag: " + c;
                    throw new IllegalArgumentException( "Unuexpected primitive flag: " + c );
            }
            return result;
        }
    }
}