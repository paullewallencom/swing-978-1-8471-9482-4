package org.grandtestauto.test;

import org.grandtestauto.TestFinder;

import java.util.SortedSet;
import java.util.TreeSet;

import jet.testtools.test.org.grandtestauto.*;

/**
 * Unit test for <code>TestFinder</code>.
 *
 * @author Tim Lavers
 */
public class TestFinderTest extends CFTestBase {

    private TestFinder pi;

    protected void init( String archiveName, String packageName ) throws Exception {
        super.init( archiveName, packageName );
        pi = new TestFinder( packageName, classesDir );
    }

    public boolean foundSomeClassesTest() throws Exception {
        init( Grandtestauto.test1_zip, "a1.test" );
        assert !pi.foundSomeClasses();
        pi.processClass(  "UnitTester" );//Name does not end "Test".
        assert !pi.foundSomeClasses();

        init( Grandtestauto.test5_zip, "a5.test" );
        pi.processClass( "XTest" );
        assert pi.foundSomeClasses();

        init( Grandtestauto.test20_zip, "a20.test" );
        pi.processClass(  "XTest" );//Abstract.
        assert !pi.foundSomeClasses();
        return true;
    }

    public boolean processClassTest() throws Exception {
        init( Grandtestauto.test1_zip, "a1.test" );
        pi.processClass(  "UnitTester" );//Name does not end "Test".
        assert pi.classesInPackage().isEmpty();

        init( Grandtestauto.test5_zip, "a5.test" );
        pi.processClass( "XTest" );
        assert pi.classesInPackage().size() == 1;
        assert pi.classesInPackage().contains( "XTest" );

        init( Grandtestauto.test20_zip, "a20.test" );
        pi.processClass(  "XTest" );//Abstract.
        assert pi.classesInPackage().isEmpty();
        return true;
    }

    public boolean classesInPackageTest() throws Exception {
        init( Grandtestauto.test33_zip, "a33.test" );
        assert pi.classesInPackage().isEmpty();
        pi.seek();
        SortedSet<String> expected = new TreeSet<String>( );
        expected.add( "ANTest");
        expected.add( "ATest");
        expected.add( "CWTest");
        expected.add( "DNCWTest");
        expected.add( "INTest");
        expected.add( "ITest");
        expected.add( "SWTest");
        expected.add( "CPWTest");
        expected.add( "DPNCWTest");
        expected.add( "EWTest");
        expected.add( "MREETest");
        expected.add( "MRETest");
        expected.add( "CPTest");
        expected.add( "DNCTest");
        expected.add( "DPNCTest");
        expected.add( "IADTest");
        expected.add( "CTest");
        assert pi.classesInPackage().equals( expected );
        return true;
    }
    public boolean constructorTest() {
        //The constructor is exercised in init(), so just return true.
        return true;
    }
}