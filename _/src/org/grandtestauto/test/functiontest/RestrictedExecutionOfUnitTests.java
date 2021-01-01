package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class RestrictedExecutionOfUnitTests extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test38_zip ), true, false, false, "a38.b", null );
        gta.runAllTests();
        assert testsRun.size() == 7;
        assert testsRun.contains( "a38.b.test.UnitTester" );
        assert testsRun.contains( "a38.c.test.UnitTester" );
        assert testsRun.contains( "a38.d.test.UnitTester" );
        assert testsRun.contains( "a38.b.a.test.UnitTester" );
        assert testsRun.contains( "a38.c.a.test.UnitTester" );
        assert testsRun.contains( "a38.b.b.test.UnitTester" );
        assert testsRun.contains( "a38.b.a.a.test.UnitTester" );

        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test38_zip ), true, false, false, null, "a38.a.a.a.a.test", null, false, true, null, null, null, null, null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 5;
        assert testsRun.contains( "a38.test.UnitTester" );
        assert testsRun.contains( "a38.a.test.UnitTester" );
        assert testsRun.contains( "a38.a.a.test.UnitTester" );
        assert testsRun.contains( "a38.a.a.a.test.UnitTester" );
        assert testsRun.contains( "a38.a.a.a.a.test.UnitTester" );

        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test38_zip ), true, false, false, "a38.a.b", "a38.a.c.test", null, false, true, null, null, null, null, null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 3;
        assert testsRun.contains( "a38.a.b.test.UnitTester" );
        assert testsRun.contains( "a38.a.b.a.test.UnitTester" );
        assert testsRun.contains( "a38.a.c.test.UnitTester" );

        init();
        gta = Helpers.setupForZip( new File( Grandtestauto.test38_zip ), true, false, false, "a38.a.b", "a38.a.c.test", "a38.a.a.a", false, true, null, null, null, null, null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 1;
        assert testsRun.contains( "a38.a.a.a.test.UnitTester" );
        return true;
    }
}
