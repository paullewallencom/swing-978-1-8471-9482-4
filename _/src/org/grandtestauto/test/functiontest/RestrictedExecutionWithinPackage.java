package org.grandtestauto.test.functiontest;

import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

import jet.testtools.test.org.grandtestauto.*;

/**
 * See the GrandTestAuto test specification.
 */
public class RestrictedExecutionWithinPackage extends FTBase {
    public boolean runTest() {
        //Sanity check that all tests run.
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), true, true, true, null, null, null,false, true, null, null, null, null, null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 15: "Got: " + testsRun;

        //Restrict to unit tests only, for a single package, and from B to D.
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), true, false, false, null, null, "a80",false, true, null, null, "B", "D", null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 3: "Got: " + testsRun;
        assert testsRun.contains( "a80.test.BTest" );
        assert testsRun.contains( "a80.test.CTest" );
        assert testsRun.contains( "a80.test.DTest" );

        //Restrict to unit tests only, for a single package, and to D.
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), true, false, false, null, null, "a80",false, true, null, null, null, "D", null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 4: "Got: " + testsRun;
        assert testsRun.contains( "a80.test.ATest" );
        assert testsRun.contains( "a80.test.BTest" );
        assert testsRun.contains( "a80.test.CTest" );
        assert testsRun.contains( "a80.test.DTest" );

        //Restrict to unit tests only, for a single package, and from B.
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), true, false, false, null, null, "a80",false, true, null, null, "B", null, null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 4: "Got: " + testsRun;
        assert testsRun.contains( "a80.test.BTest" );
        assert testsRun.contains( "a80.test.CTest" );
        assert testsRun.contains( "a80.test.DTest" );
        assert testsRun.contains( "a80.test.ETest" );

        //Restrict to unit tests only, for a single package, and running C only..
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), true, false, false, null, null, "a80",false, true, null, null, "B", "D", "C", null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 1: "Got: " + testsRun;
        assert testsRun.contains( "a80.test.CTest" );

        //Restrict to function tests only, for a single package, and from B to D.
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), false, true, false, null, null, "a80.functiontest",false, true, null, null, "B", "D", null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 3: "Got: " + testsRun;
        assert testsRun.contains( "a80.functiontest.B" );
        assert testsRun.contains( "a80.functiontest.C" );
        assert testsRun.contains( "a80.functiontest.D" );

        //Restrict to function tests only, for a single package, and to D.
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), false, true, false, null, null, "a80.functiontest",false, true, null, null, null, "D", null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 4: "Got: " + testsRun;
        assert testsRun.contains( "a80.functiontest.A" );
        assert testsRun.contains( "a80.functiontest.B" );
        assert testsRun.contains( "a80.functiontest.C" );
        assert testsRun.contains( "a80.functiontest.D" );

        //Restrict to function tests only, for a single package, and from B..
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), false, true, false, null, null, "a80.functiontest",false, true, null, null, "B", null, null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 4: "Got: " + testsRun;
        assert testsRun.contains( "a80.functiontest.B" );
        assert testsRun.contains( "a80.functiontest.C" );
        assert testsRun.contains( "a80.functiontest.D" );
        assert testsRun.contains( "a80.functiontest.E" );

        //Restrict to load tests only, for a single package, and from D to E.
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), false, false, true, null, null, "a80.loadtest",false, true, null, null, "D", "E", null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 2: "Got: " + testsRun;
        assert testsRun.contains( "a80.loadtest.D" );
        assert testsRun.contains( "a80.loadtest.E" );

        //Restrict to function tests only, for a single package, and from D to E, but also specify just a single test to be run.
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), false, true, false, null, null, "a80.functiontest",false, true, null, null, "B", "D", "A", null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 1: "Got: " + testsRun;
        assert testsRun.contains( "a80.functiontest.A" ) : "Got: " + testsRun;

        //Restrict to load tests only, for a single package, and just s single test E.
        testsRun.clear();
        gta = Helpers.setupForZip( new File( Grandtestauto.test80_zip ), false, false, true, null, null, "a80.loadtest",false, true, null, null, null, null, "E", null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 1: "Got: " + testsRun;
        assert testsRun.contains( "a80.loadtest.E" );
        return true;
    }
}
