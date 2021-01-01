package org.grandtestauto.test;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.grandtestauto.*;
import jet.testtools.test.org.grandtestauto.*;

/**
 * Unit test for <code>ClassAnalyser</code>.
 *
 * @author Tim Lavers
 */
public class ClassAnalyserTest {

    public boolean nameInTestMethodNamesTest() throws Exception {
        boolean result = true;
        //Classes representing object types.
        String str = ClassAnalyser.nameInTestMethodNames( String.class );
        result &= str.equals( "String" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( getClass() );
        result &= str.equals( "ClassAnalyserTest" );
        assert result : "Got: " + str;

        //Classes representing primitive types.
        str = ClassAnalyser.nameInTestMethodNames( boolean.class );
        result &= str.equals( "boolean" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( byte.class );
        result &= str.equals( "byte" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( char.class );
        result &= str.equals( "char" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( short.class );
        result &= str.equals( "short" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( int.class );
        result &= str.equals( "int" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( float.class );
        result &= str.equals( "float" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( long.class );
        result &= str.equals( "long" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( double.class );
        result &= str.equals( "double" );
        assert result : "Got: " + str;

        //Arrays of object types.
        str = ClassAnalyser.nameInTestMethodNames( String[].class );
        result &= str.equals( "StringArray" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( Object[].class );
        result &= str.equals( "ObjectArray" );
        assert result : "Got: " + str;

        //Arrays of primitives.
        str = ClassAnalyser.nameInTestMethodNames( boolean[].class );
        result &= str.equals( "booleanArray" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( byte[].class );
        result &= str.equals( "byteArray" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( char[].class );
        result &= str.equals( "charArray" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( short[].class );
        result &= str.equals( "shortArray" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( int[].class );
        result &= str.equals( "intArray" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( float[].class );
        result &= str.equals( "floatArray" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( long[].class );
        result &= str.equals( "longArray" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( double[].class );
        result &= str.equals( "doubleArray" );
        assert result : "Got: " + str;

        //Now for some multi-dimensional arrays.
        str = ClassAnalyser.nameInTestMethodNames( String[][].class );
        result &= str.equals( "StringArray2" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( int[][].class );
        result &= str.equals( "intArray2" );
        assert result : "Got: " + str;

        str = ClassAnalyser.nameInTestMethodNames( boolean[][][][].class );
        result &= str.equals( "booleanArray4" );
        assert result : "Got: " + str;

        return result;
    }

    public boolean testMethodNamesTest() throws Exception {
        boolean result = true;
        //This test uses the classes in the test package a8.
        Helpers.cleanTempDirectory();
        File zip = new File( Grandtestauto.test8_zip );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        Class x = Class.forName( "a8.X" );
        ClassAnalyser ca = new ClassAnalyser( x );

        TreeSet<String> expected = new TreeSet<String>();
        //The tests for the accessible methods.
        expected.add( "aTest" );
        expected.add( "bTest" );
        expected.add( "b_int_int_Test" );
        expected.add( "b_String_Test" );
        expected.add( "b_StringArray_int_intArray_Test" );
        //The tests for the constructors.
        expected.add( "constructorTest" );
        expected.add( "constructor_String_Test" );
        expected.add( "constructor_X_Test" );
        expected.add( "constructor_charArray_Test" );
        Helpers.assertEqual( ca.testMethodNames(), expected );

        //Now let's look at methods from Y.
        Class y = Class.forName( "a8.Y" );
        ca = new ClassAnalyser( y );
        //The main thing to check here is that, since there are
        //now two methods called 'a', a compound test name is needed
        //for the version of 'a' that takes params.
        expected = new TreeSet<String>();
        //Methods inherited from X.
        expected.add( "bTest" );
        expected.add( "b_int_int_Test" );
        expected.add( "b_String_Test" );
        expected.add( "b_StringArray_int_intArray_Test" );
        //Compound names for methods that now need them.
        expected.add( "a_ObjectArray4_Test" );
        expected.add( "aTest" );
        //New method.
        expected.add( "cTest" );
        //Constructors.
        expected.add( "constructorTest" );
        Helpers.assertEqual( ca.testMethodNames(), expected );

        return result;
    }

    public boolean isTestableTest() throws Exception {
        //Expand the zip archive into the temp directory.
        Helpers.cleanTempDirectory();
        File zip = new File(Grandtestauto.test6_zip );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        boolean b = true;
        Class<?> x = Class.forName( "a6.X" );
        //First the public statics.
        Method meth = x.getDeclaredMethod( "a" );
        ClassAnalyser ca = new ClassAnalyser( x );
        b &= ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "b" );
        b &= ca.isTestable( meth );
        assert b;
        //The protected statics.
        meth = x.getDeclaredMethod( "c" );
        b &= ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "d" );
        b &= ca.isTestable( meth );
        assert b;
        //Package statics. Not testable.
        meth = x.getDeclaredMethod( "e" );
        b &= !ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "f" );
        b &= !ca.isTestable( meth );
        assert b;
        //Private statics. Not testable.
        meth = x.getDeclaredMethod( "g" );
        b &= !ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "h" );
        b &= !ca.isTestable( meth );
        assert b;
        //Public instance methods.
        meth = x.getDeclaredMethod( "m" );
        b &= ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "n" );
        b &= ca.isTestable( meth );
        assert b;
        //Protected instance methods
        meth = x.getDeclaredMethod( "o" );
        b &= ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "p" );
        b &= ca.isTestable( meth );
        assert b;
        //Protected final instance method
        meth = x.getDeclaredMethod( "pf" );
        b &= !ca.isTestable( meth );
        assert b;
        //Package level instance methods.
        meth = x.getDeclaredMethod( "q" );
        b &= !ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "r" );
        b &= !ca.isTestable( meth );
        assert b;
        //Privates.
        meth = x.getDeclaredMethod( "s" );
        b &= !ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "t" );
        b &= !ca.isTestable( meth );
        assert b;

        //Abstract methods.
        meth = x.getDeclaredMethod( "y" );
        b &= !ca.isTestable( meth );
        assert b;
        meth = x.getDeclaredMethod( "z" );
        b &= !ca.isTestable( meth );
        assert b;

        //Now a class that has mostly the same methods but is final.
//        zip = new File( ), "test6.zip" );
//        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        Class<?> w = Class.forName( "a6.W" );
        ca = new ClassAnalyser( w );
        //First the public statics.
        meth = w.getDeclaredMethod( "a" );
        b &= ca.isTestable( meth );
        assert b;
        meth = w.getDeclaredMethod( "b" );
        b &= ca.isTestable( meth );
        assert b;
        //The protected statics.
        meth = w.getDeclaredMethod( "c" );
        b &= !ca.isTestable( meth );
        assert b;
        meth = w.getDeclaredMethod( "d" );
        b &= !ca.isTestable( meth );
        assert b;
        //Package statics. Not testable.
        meth = w.getDeclaredMethod( "e"  );
        b &= !ca.isTestable( meth );
        assert b;
        meth = w.getDeclaredMethod( "f"  );
        b &= !ca.isTestable( meth );
        assert b;
        //Private statics. Not testable.
        meth = w.getDeclaredMethod( "g"  );
        b &= !ca.isTestable( meth );
        assert b;
        meth = w.getDeclaredMethod( "h"  );
        b &= !ca.isTestable( meth );
        assert b;
        //Public instance methods.
        meth = w.getDeclaredMethod( "m"  );
        b &= ca.isTestable( meth );
        assert b;
        meth = w.getDeclaredMethod( "n"  );
        b &= ca.isTestable( meth );
        assert b;
        //Protected instance methods
        meth = w.getDeclaredMethod( "o"  );
        b &= !ca.isTestable( meth );
        assert b;
        meth = w.getDeclaredMethod( "p"  );
        b &= !ca.isTestable( meth );
        assert b;
        //Package level instance methods.
        meth = w.getDeclaredMethod( "q"  );
        b &= !ca.isTestable( meth );
        assert b;
        meth = w.getDeclaredMethod( "r"  );
        b &= !ca.isTestable( meth );
        assert b;
        //Privates.
        meth = w.getDeclaredMethod( "s"  );
        b &= !ca.isTestable( meth );
        assert b;
        meth = w.getDeclaredMethod( "t"  );
        b &= !ca.isTestable( meth );
        assert b;

        //Use the package a18.
        Helpers.cleanTempDirectory();
        zip = new File( Grandtestauto.test18_zip );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        x = Class.forName( "a18.X" );
        //First the declared compareTo method.
        meth = x.getDeclaredMethod( "compareTo", x );
        ca = new ClassAnalyser( x );
        b &= ca.isTestable( meth );
        //Now the synthetic method compareTo( Object );
        meth = x.getDeclaredMethod( "compareTo", Object.class );
        assert !ca.isTestable( meth );

        assert b;
        return b;
    }

    public boolean testableMethodsTest() throws Exception {
        //Expand the zip archive into the temp directory.
        Helpers.cleanTempDirectory();
        File zip = new File( Grandtestauto.test6_zip );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        boolean b = true;
        Class x = Class.forName( "a6.X" );
        ClassAnalyser ca = new ClassAnalyser( x );
        Collection<Method> meths = ca.testableMethods();
        b &= meths.size() == 8;
        assert b;
        Set<String> s = new HashSet<String>();
        for (Method meth : meths ) {
            s.add( meth.getName() );
        }
        b &= s.contains( "a" );
        assert b;
        b &= s.contains( "b" );
        b &= s.contains( "c" );
        b &= s.contains( "d" );
        b &= s.contains( "m" );
        b &= s.contains( "n" );
        b &= s.contains( "o" );
        b &= s.contains( "p" );
        assert b;

        //Now, a6.Y should have all the same testable methods as a6.X
        //as well as y, z.
        Class y = Class.forName( "a6.Y" );
        ca = new ClassAnalyser( y );
        Collection<Method> t = ca.testableMethods();
        b &= t.size() == 10;
        assert b;
        s = new HashSet<String>();
        for (Method method : t ) {
            s.add( method.getName() );
        }
        b &= s.contains( "a" );
        assert b;
        b &= s.contains( "b" );
        b &= s.contains( "c" );
        b &= s.contains( "d" );
        b &= s.contains( "m" );
        b &= s.contains( "n" );
        b &= s.contains( "o" );
        b &= s.contains( "p" );
        b &= s.contains( "y" );
        b &= s.contains( "z" );
        return b;
    }

    public boolean constructorTest() throws Exception {
        //Not much to test.
        boolean result = true;
        //Expand the zip archive into the temp directory.
        Helpers.cleanTempDirectory();
        File zip = new File( Grandtestauto.test6_zip );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        Class x = Class.forName( "a6.X" );
        ClassAnalyser ca = new ClassAnalyser( x );
        result &= ca.analysedClass().equals( x );
        return result;
    }

    public boolean analysedClassTest() throws Exception {
        //Expand the zip archive into the temp directory.
        Helpers.cleanTempDirectory();
        File zip = new File( Grandtestauto.test6_zip );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        Class x = Class.forName( "a6.X" );
        ClassAnalyser ca = new ClassAnalyser( x );
        return ca.analysedClass().equals( x );
    }

    public boolean testableConstructorsTest() throws Exception {
        boolean result = true;
        //This test uses the classes in the test package a8.
        Helpers.cleanTempDirectory();
        File zip = new File( Grandtestauto.test8_zip );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        Class x = Class.forName( "a8.X" );
        ClassAnalyser ca = new ClassAnalyser( x );
        //Here are the constructors:
        //public X() {}
        //public X( String str ) {}
        //public X( char[] c ) {}
        //public X( X x ) {}
        Collection<Constructor> constructors = ca.testableConstructors();
        Helpers.assertEqual( constructors.size(), 4 );
        Set<String> s = new HashSet<String>();
        for (Constructor meth : constructors ) {
            s.add( meth.toString());
        }
        Set<String> expected = new HashSet<String>();
        expected.add( "public a8.X()" );
        expected.add( "public a8.X(java.lang.String)" );
        expected.add( "public a8.X(a8.X)" );
        expected.add( "public a8.X(char[])" );
        Helpers.assertEqual( s, expected );
        return result;
    }
}