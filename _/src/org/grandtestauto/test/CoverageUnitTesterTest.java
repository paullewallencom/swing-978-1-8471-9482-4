package org.grandtestauto.test;

import jet.testtools.*;
import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Unit test for <code>CoverageUnitTester</code>.
 *
 * @author Tim Lavers
 */
public class CoverageUnitTesterTest {

    private static Set<String> methodsCalled = new HashSet<String>();
    private static Set<String> testsCreated = new HashSet<String>();

    public static void ping( String methodCalled ) {
        methodsCalled.add( methodCalled );
    }

    public static void recordTestCreated( Class testClass ) {
        testsCreated.add( testClass.getName() );
    }

    public boolean unitTestIdentificationTest() throws Exception {
        init();
        CoverageUnitTester cut = cutForConfiguredPackage( Grandtestauto.test46_zip, "a46.test", null, null, null, null, null );
        cut.runTests();
        assert testsCreated.contains( "a46.test.XTest" );
        assert testsCreated.contains( "a46.test.ATest" );
        String logFileContents = Helpers.logFileContents();
        String msgForYTest = Messages.message( Messages.OPK_COULD_NOT_CREATE_TEST_CLASS, "a46.test.YTest" );
        assert logFileContents.contains( msgForYTest );
        String msgForZTest = Messages.message( Messages.OPK_COULD_NOT_CREATE_TEST_CLASS, "a46.test.ZTest" );
        assert logFileContents.contains( msgForZTest );

        return true;
    }

    public boolean runTestsTest() throws Exception {
        init();

        /* Unit Test Identification */
        CoverageUnitTester cut = cutForConfiguredPackage( Grandtestauto.test46_zip, "a46.test", null, null, null, null, null );
        cut.runTests();
        assert testsCreated.contains( "a46.test.XTest" );
        assert testsCreated.contains( "a46.test.ATest" );
        String logFileContents = Helpers.logFileContents();
        String msgForYTest = Messages.message( Messages.OPK_COULD_NOT_CREATE_TEST_CLASS, "a46.test.YTest" );
        assert logFileContents.contains( msgForYTest );
        String msgForZTest = Messages.message( Messages.OPK_COULD_NOT_CREATE_TEST_CLASS, "a46.test.ZTest" );
        assert logFileContents.contains( msgForZTest );

        /* Test Method Identification */
        methodsCalled = new HashSet<String>();
        cut = cutForConfiguredPackage( Grandtestauto.test17_zip, "a17.test", null, null, null, null, null );
        cut.runTests();//This unit test does not actually pass, no matter.
        Set<String> expected = new HashSet<String>();
        expected.add( "mTest" );
        expected.add( "nTest" );
        Helpers.assertEqual( methodsCalled, expected );

        /* Test Returns False */
        cut = cutForConfiguredPackage( Grandtestauto.test14_zip, "a14.test", null, null, null, null, null );
        assert !cut.runTests();

        /* Exception Thrown by Test */
        System.out.println( ">>>>>> EXPECTED STACK TRACE START <<<<<<" );
        cut = cutForConfiguredPackage( Grandtestauto.test23_zip, "a23.test", null, null, null, null, null );
        assert !cut.runTests();
        System.out.println( ">>>>>> EXPECTED STACK TRACE END <<<<<<" );

        /* Tests for Methods */
        //Public method not tested.
        cut = cutForConfiguredPackage( Grandtestauto.test5_zip, "a5.test", null, null, null, null, null );
        assert !cut.runTests();
        logFileContents = Helpers.logFileContents();
        //This tests the locale specific form of the error message. Will only work in _en locales.
        String msgForMissingTest = "In a5 the following method is not unit-tested:" + Helpers.NL + "public void a5.X.m()";
        assert logFileContents.contains( msgForMissingTest ) : logFileContents;

        //Test for overridden method missing.
        cut = cutForConfiguredPackage( Grandtestauto.test26_zip, "a26.test", null, null, null, null, null );
        assert !cut.runTests();
        logFileContents = Helpers.logFileContents();
        msgForMissingTest = "In a26 the following method is not unit-tested:" + Helpers.NL + "protected void a26.A.a()";
        assert logFileContents.contains( msgForMissingTest ) : logFileContents;

        //Overridden method from abstract class missing test
        cut = cutForConfiguredPackage( Grandtestauto.test27_zip, "a27.test", null, null, null, null, null );
        assert !cut.runTests();
        logFileContents = Helpers.logFileContents();
        msgForMissingTest = "In a27 the following method is not unit-tested:" + Helpers.NL + "protected void a27.A.a()";
        assert logFileContents.contains( msgForMissingTest ) : logFileContents;

        //Another example of overridden method not being tested.
        cut = cutForConfiguredPackage( Grandtestauto.test28_zip, "a28.test", null, null, null, null, null );
        assert !cut.runTests();
        logFileContents = Helpers.logFileContents();
        msgForMissingTest = "In a28 the following method is not unit-tested:" + Helpers.NL + "protected void a28.A.a()";
        assert logFileContents.contains( msgForMissingTest ) : logFileContents;

        //Yet another example of overridden method not being tested.
        cut = cutForConfiguredPackage( Grandtestauto.test32_zip, "a32.test", null, null, null, null, null );
        assert !cut.runTests();
        logFileContents = Helpers.logFileContents();
        msgForMissingTest = "In a32 the following method is not unit-tested:" + Helpers.NL + "protected void a32.A.a()";
        assert logFileContents.contains( msgForMissingTest ) : logFileContents;

        //Overridden method is tested in test for abstract class.
        cut = cutForConfiguredPackage( Grandtestauto.test29_zip, "a29.test", null, null, null, null, null );
        assert cut.runTests();

        //Overridden method is tested in test for concrete class.
        cut = cutForConfiguredPackage( Grandtestauto.test30_zip, "a30.test", null, null, null, null, null );
        assert cut.runTests();

        //Overridden method is tested in test for concrete class, no test for the abstract class.
        cut = cutForConfiguredPackage( Grandtestauto.test31_zip, "a31.test", null, null, null, null, null );
        assert cut.runTests();

        /* Synthetic Methods do not Need Testing */
        cut = cutForConfiguredPackage( Grandtestauto.test18_zip, "a18.test", null, null, null, null, null );
        assert cut.runTests();

        /* Some Enum Methods do not Need Testing */
        cut = cutForConfiguredPackage( Grandtestauto.test19_zip, "a19.test", null, null, null, null, null );
        assert cut.runTests();

        /* Stop at First Failure Within Unit Test */
        cut = cutForConfiguredPackage( Grandtestauto.test19_zip, "a19.test", null, null, null, null, null );
        assert cut.runTests();

        /* Name Mangling for Method Tests*/
        cut = cutForConfiguredPackage( Grandtestauto.test47_zip, "a47.test", null, null, null, null, null );
        cut.runTests();
        logFileContents = Helpers.logFileContents();
        String listOfUntestedMethods = "In a47 the following methods are not unit-tested:" + Helpers.NL +
                "public void a47.Y.c(java.lang.String[],int,int[])" + Helpers.NL +
                "public void a47.X.b(int,int)" + Helpers.NL +
                "public void a47.W.a(java.lang.String).";
        assert logFileContents.contains( listOfUntestedMethods ) : "Got: '" + logFileContents;

        /* Name Mangling for VarArgs Methods */
        cut = cutForConfiguredPackage( Grandtestauto.test16_zip, "a16.test", null, null, null, null, null );
        methodsCalled = new HashSet<String>();
        assert cut.runTests();
        Helpers.assertEqual( methodsCalled.size(), 4 );
        Set<String> expectedMethodsCalled = new HashSet<String>();
        expectedMethodsCalled.add( "a_String_Test" );
        expectedMethodsCalled.add( "a_StringArray_Test" );
        expectedMethodsCalled.add( "a_StringArray_StringArray_Test" );
        expectedMethodsCalled.add( "a_String_StringArray_Test" );
        Helpers.assertEqual( methodsCalled, expectedMethodsCalled );

        /* Name Mangling not Needed on Account of Hidden Methods */
        cut = cutForConfiguredPackage( Grandtestauto.test48_zip, "a48.test", null, null, null, null, null );
        assert cut.runTests();

        /* Testable Classes */
        cut = cutForConfiguredPackage( Grandtestauto.test4_zip, "a4.test", null, null, null, null, null );
        assert !cut.runTests();
        logFileContents = Helpers.logFileContents();
        msgForMissingTest = "In a4 the following class is not unit-tested:" + Helpers.NL + "X";
        assert logFileContents.contains( msgForMissingTest ) : logFileContents;

        /* Inner Classes not Tested */
        cut = cutForConfiguredPackage( Grandtestauto.test24_zip, "a24.test", null, null, null, null, null );
        assert cut.runTests();

        /* RMI Stub Classes not Tested */
        cut = cutForConfiguredPackage( Grandtestauto.test22_zip, "a22.test", null, null, null, null, null );
        assert cut.runTests();

        /* Classes annotated with DoesNotNeedTest do not need test */
        cut = cutForConfiguredPackage( Grandtestauto.test86_zip, "a86.test", null, null, null, null, null );
        assert cut.runTests();

        /* Bug whereby a package with no testable classes caused a NullPointerException. */
        cut = cutForConfiguredPackage( Grandtestauto.test87_zip, "a87.test", null, null, null, null, null );
        assert cut.runTests();

        /* Constructors for Testable Classes Need Testing */
        //protected constructor untested
        cut = cutForConfiguredPackage( Grandtestauto.test12_zip, "a12.test", null, null, null, null, null );
        assert !cut.runTests();
        logFileContents = Helpers.logFileContents();
        msgForMissingTest = "In a12 the following constructor was not tested:" + Helpers.NL + "protected a12.X()";
        assert logFileContents.contains( msgForMissingTest ) : logFileContents;

        //name mangling
        cut = cutForConfiguredPackage( Grandtestauto.test49_zip, "a49.test", null, null, null, null, null );
        assert !cut.runTests();
        logFileContents = Helpers.logFileContents();
        System.out.println( "resultsFileContents = " + logFileContents );
        String listOfUntestedConstructors = "In a49 the following constructors were not tested:" + Helpers.NL +
                "public a49.W(java.lang.String)" + Helpers.NL +
                "public a49.X(int,int)" + Helpers.NL +
                "public a49.Y(java.lang.String[],int,int[])" + Helpers.NL +
                "public a49.Z(java.lang.String).";
        assert logFileContents.contains( listOfUntestedConstructors );

        //success
        cut = cutForConfiguredPackage( Grandtestauto.test50_zip, "a50.test", null, null, null, null, null );
        assert !cut.runTests();

        /* Other tests. */
        //protected method untested
        cut = cutForConfiguredPackage( Grandtestauto.test11_zip, "a11.test", null, null, null, null, null );
        assert !cut.runTests();

        //re-definition of method not tested
        cut = cutForConfiguredPackage( Grandtestauto.test13_zip, "a13.test", null, null, null, null, null );
        assert !cut.runTests();

        //Complex hierarchy based on a real example.
        cut = cutForConfiguredPackage( Grandtestauto.test33_zip, "a33.test", null, null, null, null, null );
        assert cut.runTests();

        return true;
    }

    /**
     * This test is to help find a problem with one of the function tests.
     */
    public boolean a1Test() {
        CoverageUnitTester cut = cutForConfiguredPackage( Grandtestauto.test1_zip, "a1.test", null, null, null, null, null );
        assert !cut.runTests();
        String log = Helpers.logFileContents();
        String errMsg = "In a1 the following classes are not unit-tested:" + Helpers.NL +
                "Y" + Helpers.NL +
                "X.";
        assert log.contains( errMsg ) : "Got: '" + log + "'";
        return true;
    }

    public boolean overriddentParametrisedMethodTest() {
        CoverageUnitTester cut = cutForConfiguredPackage( Grandtestauto.test84_zip, "a84.test", null, null, null, null, null );
        assert cut.runTests() : "log was: " + Helpers.logFileContents();
        return true;
    }

    public boolean restrictionOfMethodsRunTest() {
        //First a sanity check: with no restrictions, all tests run.
        CoverageUnitTester cut = cutForConfiguredPackage( Grandtestauto.test83_zip, "a83.test", null, null, null, null, null );
        methodsCalled.clear();
        cut.runTests();
        Set<String> expected = new HashSet<String>();
        expected.add( "constructor_A_Test" );
        expected.add( "aTest" );
        expected.add( "bTest" );
        expected.add( "cTest" );
        expected.add( "dTest" );
        expected.add( "eTest" );
        expected.add( "constructor_B_Test" );
        expected.add( "xTest" );
        expected.add( "x_int_Test" );
        expected.add( "x_String_Test" );
        expected.add( "yTest" );
        expected.add( "y_intArray_Test" );
        assert methodsCalled.equals( expected ) : "Got: " + methodsCalled;

        //Run just ATest, and only methods b to dTest.
        cut = cutForConfiguredPackage( Grandtestauto.test83_zip, "a83.test", "a83", "A", "b", "dTest", null );
        methodsCalled.clear();
        cut.runTests();
        expected = new HashSet<String>();
        expected.add( "bTest" );
        expected.add( "cTest" );
        expected.add( "constructor_A_Test" );
        expected.add( "dTest" );
        assert methodsCalled.equals( expected ) : "Got: " + methodsCalled;

        //Run just ATest, and only methods to d.
        cut = cutForConfiguredPackage( Grandtestauto.test83_zip, "a83.test", "a83", "A", null, "d", null );
        methodsCalled.clear();
        cut.runTests();
        expected = new HashSet<String>();
        expected.add( "aTest" );
        expected.add( "bTest" );
        expected.add( "cTest" );
        expected.add( "constructor_A_Test" );
        assert methodsCalled.equals( expected ) : "Got: " + methodsCalled;

        //Run just ATest, and only methods from c.
        cut = cutForConfiguredPackage( Grandtestauto.test83_zip, "a83.test", "a83", "A", "c", null, null );
        methodsCalled.clear();
        cut.runTests();
        expected = new HashSet<String>();
        expected.add( "cTest" );
        expected.add( "constructor_A_Test" );
        expected.add( "dTest" );
        expected.add( "eTest" );
        assert methodsCalled.equals( expected ) : "Got: " + methodsCalled;

        //Run just ATest, and only cTest..
        cut = cutForConfiguredPackage( Grandtestauto.test83_zip, "a83.test", "a83", "A", null, null, "cTest" );
        methodsCalled.clear();
        cut.runTests();
        expected = new HashSet<String>();
        expected.add( "cTest" );
        assert methodsCalled.equals( expected ) : "Got: " + methodsCalled;

        //Single method overrides other settings
        cut = cutForConfiguredPackage( Grandtestauto.test83_zip, "a83.test", "a83", "A", "a", "d", "cTest" );
        methodsCalled.clear();
        cut.runTests();
        expected = new HashSet<String>();
        expected.add( "cTest" );
        assert methodsCalled.equals( expected ) : "Got: " + methodsCalled;

        return true;
    }

    public boolean constructor_GrandTestAuto_Test() throws Exception {
        GrandTestAuto gta = Helpers.setupForZip( Grandtestauto.test4_zip );
        Class<?> ut = Class.forName( "a4.test.UnitTester" );
        //Check that the flag is initially false.
        Field flag = ut.getDeclaredField( "flag" );
        assert !flag.getBoolean( null ) : "Flag not initially false";
        Constructor constructor = ut.getConstructor( GrandTestAuto.class );
        CoverageUnitTester cut = (CoverageUnitTester) constructor.newInstance( gta );
        assert !cut.runTests();
        return true;
    }

    public boolean noPrintoutOfMissingTestsWhenTestsAreRestrictedTest() {
        CoverageUnitTester cut = cutForConfiguredPackage( Grandtestauto.test83_zip, "a83.test", "a83", "A", "b", "dTest", null );
        cut.runTests();
        String log = Helpers.logFileContents();
        System.out.println( "log = " + log );
        return true;
    }

    public boolean constructor_String_Test() throws Exception {
        //This test works by calling the main method of
        //test15.test.UnitTester which uses the constructor
        //from string. Calling main should run the unit
        //tests and cause "aTest", "bTest", "cTest" and
        //"constructorTest" to be in methodsCalled.
        //Also, a public static boolean of a15.test.UnitTester
        //will be set to true if the tests pass.
        runPackageTest( Grandtestauto.test15_zip, "a15", true );

        //Check the pinging....
        assert methodsCalled.size() == 4;
        assert methodsCalled.contains( "aTest" );
        assert methodsCalled.contains( "bTest" );
        assert methodsCalled.contains( "cTest" );
        assert methodsCalled.contains( "constructorTest" );
        return true;
    }

    public boolean paramsTest() throws Exception {
        methodsCalled = new HashSet<String>();
        runPackageTest( Grandtestauto.test34_zip, "a34", true );
        Helpers.assertEqual( methodsCalled.size(), 3 );
        Set<String> expected = new HashSet<String>();
        expected.add( "aTest" );
        expected.add( "a_String_Test" );
        expected.add( "a_Frame_Test" );
        Helpers.assertEqual( methodsCalled, expected );

        return true;
    }

    private void runPackageTest( String zipName, String packageName, boolean expectedResult ) throws Exception {
        Helpers.setupForZip( zipName );
        Class<?> ut = Class.forName( packageName + ".test.UnitTester" );
        //Check that the flag is initially false.
        Field flag = ut.getDeclaredField( "flag" );
        assert !flag.getBoolean( null ) : "Flag not initially false";
        Method main = ut.getDeclaredMethod( "main", String[].class );
        Object[] args = new Object[1];
        args[0] = new String[]{Helpers.tempDirectory().getPath()};
        main.invoke( null, args );
        assert flag.getBoolean( null ) == expectedResult;
    }

    private CoverageUnitTester cutForConfiguredPackage( String zipName, String packageName, String singlePackage, String singleTest, String initialTestMethod, String finalTestMethod, String singleTestMethod ) {
        GrandTestAuto gta = Helpers.setupForZip( new File( zipName ), true, true, true, null, null, singlePackage, false, true, Helpers.defaultLogFile().getPath(), null, null, null, singleTest, initialTestMethod, finalTestMethod, singleTestMethod );
        try {
            Class<?> ut = Class.forName( packageName + ".UnitTester" );
            Constructor ctr = ut.getConstructor( GrandTestAuto.class );
            return (CoverageUnitTester) ctr.newInstance( gta );
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "See above stack trace";
        }
        return null;
    }

    private void init() {
        methodsCalled = new HashSet<String>();
        testsCreated = new HashSet<String>();
    }
}