package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class RestrictedExecution extends FTBase {
    public boolean runTest() {
        //Unit tests only.
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test36_zip ), true, false, false );
        gta.runAllTests();
        assert testsRun.size() == 1;
        assert testsRun.get( 0 ).equals( "a36.test.UnitTester" );

        //Function tests only
        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test36_zip ), false, true, false );
        gta.runAllTests();
        assert testsRun.size() == 1;
        assert testsRun.get( 0 ).equals( "a36.functiontest.FT" );

        //Load tests only
        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test36_zip ), false, false, true );
        gta.runAllTests();
        assert testsRun.size() == 1;
        assert testsRun.get( 0 ).equals( "a36.loadtest.LT" );

        //Unit and Function tests.
        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test36_zip ), true, true, false );
        gta.runAllTests();
        assert testsRun.size() == 2;
        assert testsRun.get( 0 ).equals( "a36.test.UnitTester" );
        assert testsRun.get( 1 ).equals( "a36.functiontest.FT" );

        //Unit and Load tests
        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test36_zip ), true, false, true );
        gta.runAllTests();
        assert testsRun.size() == 2;
        assert testsRun.get( 0 ).equals( "a36.test.UnitTester" );
        assert testsRun.get( 1 ).equals( "a36.loadtest.LT" );

        //Function and Load tests
        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test36_zip ), false, true, true );
        gta.runAllTests();
        assert testsRun.size() == 2;
        assert testsRun.get( 0 ).equals( "a36.functiontest.FT" );
        assert testsRun.get( 1 ).equals( "a36.loadtest.LT" );

        //Unit, Function and Load tests.
        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test36_zip ), true, true, true );
        gta.runAllTests();
        assert testsRun.size() == 3;
        assert testsRun.get( 0 ).equals( "a36.test.UnitTester" );
        assert testsRun.get( 1 ).equals( "a36.functiontest.FT" );
        assert testsRun.get( 2 ).equals( "a36.loadtest.LT" );

        return true;
    }
}
