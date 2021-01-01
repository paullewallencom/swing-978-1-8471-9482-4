package org.grandtestauto.test;

import org.grandtestauto.PackageInfo;
import org.grandtestauto.Testability;

import java.util.Iterator;
import java.util.TreeMap;

import jet.testtools.test.org.grandtestauto.*;

/**
 * Unit test for <code>PackageInfo</code>.
 *
 * @author Tim Lavers
 */
public class PackageInfoTest extends CFTestBase {

    private PackageInfo pi;

    protected void init( String archiveName, String packageName ) throws Exception {
        super.init( archiveName, packageName );
        pi = new PackageInfo( packageName, classesDir );
    }

    public boolean processClassTest() throws Exception {
        init( Grandtestauto.test1_zip, "a1" );
        TreeMap<String, Testability> expected = new TreeMap<String, Testability>();
        assert pi.classNameToTestability().equals( expected );
        pi.processClass( "X"  );
        pi.processClass( "Y" );
        expected.put( "X", Testability.TEST_REQUIRED );
        expected.put( "Y", Testability.TEST_REQUIRED );
        assert pi.classNameToTestability().equals( expected );

        //Not public.
        pi.processClass( "X"  );
        assert pi.classNameToTestability().equals( expected );

        //Abstract. The concrete methods will be tested in tests of subclasses.
        init( Grandtestauto.test6_zip, "a6" );
        expected = new TreeMap<String, Testability>();
        pi.processClass( "X" );
        expected.put( "X", Testability.CONTAINS_TESTABLE_METHODS );
        assert pi.classNameToTestability().equals( expected );

        //An enum with no added methods.
        init( Grandtestauto.test19_zip, "a19" );
        pi.processClass( "X" );
        assert pi.classNameToTestability().isEmpty();

        //A public class with no accessible methods or constructors.
        init( Grandtestauto.test21_zip, "a21" );
        pi.processClass( "X" );
        assert pi.classNameToTestability().isEmpty();

        //An rmi _Stub class.
        init( Grandtestauto.test22_zip, "a22" );
        pi.processClass( "RemoteRI_Stub" );
        assert pi.classNameToTestability().isEmpty();

        //A class with no accessible constructors and methods needs no test.
        init( Grandtestauto.test25_zip, "a25" );
        pi.processClass( "X" );
        assert pi.classNameToTestability().isEmpty();
        return true;
    }

    public boolean constructorTest() {
        //The constructor is exercised by the init() method of
        //this test, so just return true.
        return true;
    }

    public boolean classNameToTestabilityTest() throws Exception {
        init( Grandtestauto.test1_zip, "a1" );
        pi.seek();
        //In a1 the classes are X, Y (Z not public).
        //Check the order.
        Iterator<String> itor = pi.classNameToTestability().keySet().iterator();
        assert itor.next().equals( "X" );
        assert pi.classNameToTestability().get( "X" ).equals( Testability.TEST_REQUIRED );
        assert itor.next().equals( "Y" );
        assert pi.classNameToTestability().get( "Y" ).equals( Testability.TEST_REQUIRED );
        assert !itor.hasNext();

        //In a1.b.e.g there is X and also a (misplaced) UnitTester, both public.
        init( Grandtestauto.test1_zip, "a1.b.e.g" );
        pi.seek();
        itor = pi.classNameToTestability().keySet().iterator();
        assert itor.next().equals( "UnitTester" );
        assert pi.classNameToTestability().get( "UnitTester" ).equals( Testability.TEST_REQUIRED );
        assert itor.next().equals( "X" );
        assert pi.classNameToTestability().get( "X" ).equals( Testability.TEST_REQUIRED );
        assert !itor.hasNext();
        return true;
    }

    public boolean foundSomeClassesTest() throws Exception {
        init( Grandtestauto.test1_zip, "a1" );
        assert !pi.foundSomeClasses();
        pi.seek();
        assert pi.foundSomeClasses();

        init( Grandtestauto.test1_zip, "a1.b.e.g" );
        assert !pi.foundSomeClasses();
        pi.seek();
        assert pi.foundSomeClasses();
        return true;
    }
}