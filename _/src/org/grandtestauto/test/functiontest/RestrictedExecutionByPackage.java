package org.grandtestauto.test.functiontest;

import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.util.*;
import java.io.*;

import jet.testtools.test.org.grandtestauto.*;

/**
 * See the GrandTestAuto test specification.
 */
public class RestrictedExecutionByPackage extends FTBase {

    public boolean runTest() {
        ExceptionHandling.classNameToErrorKeyToThrow = new HashMap<String, String>();
        testsRun = new LinkedList<String>();

        Set<String> expectedTestsRun = new HashSet<String>();
        //No restrictionsontests so they all run.
        expectedTestsRun.add( "a44.a.test.UnitTester" );
        expectedTestsRun.add( "a44.b.test.UnitTester" );
        expectedTestsRun.add( "a44.c.test.UnitTester" );
        expectedTestsRun.add( "a44.a.functiontest.FT" );
        expectedTestsRun.add( "a44.b.functiontest.FT" );
        expectedTestsRun.add( "a44.c.functiontest.FT" );
        expectedTestsRun.add( "a44.a.loadtest.LT" );
        expectedTestsRun.add( "a44.b.loadtest.LT" );
        expectedTestsRun.add( "a44.c.loadtest.LT" );
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true );
        assert gta.runAllTests();//Sanity check.
        assert testsRun.size() == 9;
        assert testsRun.containsAll( expectedTestsRun );

        //Initial package set.
        expectedTestsRun = new HashSet<String>();
        testsRun = new LinkedList<String>();
        expectedTestsRun.add( "a44.b.test.UnitTester" );
        expectedTestsRun.add( "a44.c.test.UnitTester" );
        expectedTestsRun.add( "a44.b.functiontest.FT" );
        expectedTestsRun.add( "a44.c.functiontest.FT" );
        expectedTestsRun.add( "a44.b.loadtest.LT" );
        expectedTestsRun.add( "a44.c.loadtest.LT" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true, "a44.b", null, null, false, false, null, null, null, null, null, null, null, null );
        assert gta.runAllTests();//Sanity check.
        assert testsRun.size() == 6;
        assert testsRun.containsAll( expectedTestsRun );

        //Final package set.
        expectedTestsRun = new HashSet<String>();
        testsRun = new LinkedList<String>();
        expectedTestsRun.add( "a44.a.test.UnitTester" );
        expectedTestsRun.add( "a44.b.test.UnitTester" );
        expectedTestsRun.add( "a44.a.functiontest.FT" );
        expectedTestsRun.add( "a44.b.functiontest.FT" );
        expectedTestsRun.add( "a44.a.loadtest.LT" );
        expectedTestsRun.add( "a44.b.loadtest.LT" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true, null, "a44.b.loadtest",null, false, false, null, null, null, null, null, null, null, null );
        assert gta.runAllTests();//Sanity check.
        assert testsRun.size() == 6;
        assert testsRun.containsAll( expectedTestsRun );

        //Initial and final package set.
        expectedTestsRun = new HashSet<String>();
        testsRun = new LinkedList<String>();
        expectedTestsRun.add( "a44.b.test.UnitTester" );
        expectedTestsRun.add( "a44.b.functiontest.FT" );
        expectedTestsRun.add( "a44.b.loadtest.LT" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true, "a44.b", "a44.b.loadtest",null, false, false, null, null, null, null, null, null, null, null );
        assert gta.runAllTests();//Sanity check.
        assert testsRun.size() == 3;
        assert testsRun.containsAll( expectedTestsRun );

        //Initial, final and single package set.
        expectedTestsRun = new HashSet<String>();
        testsRun = new LinkedList<String>();
        expectedTestsRun.add( "a44.c.functiontest.FT" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true, "a44.b", "a44.b.loadtest","a44.c.functiontest", false, false, null, null, null, null, null, null, null, null );
        assert gta.runAllTests();//Sanity check.
        assert testsRun.size() == 1;
        assert testsRun.containsAll( expectedTestsRun );
        return true;
    }
}
