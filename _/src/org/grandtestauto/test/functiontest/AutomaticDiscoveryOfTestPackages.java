package org.grandtestauto.test.functiontest;

import static jet.testtools.test.org.grandtestauto.Grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class AutomaticDiscoveryOfTestPackages extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( test38_zip ) );
        gta.runAllTests();
        assert testsRun.size() == 48;
        assert testsRun.indexOf( "a38.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.a.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.b.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.c.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.d.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.a.a.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.a.b.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.a.c.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.b.a.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.b.b.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.c.a.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.a.a.a.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.a.a.b.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.b.a.a.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.a.a.a.test.UnitTester" ) < 16;
        assert testsRun.indexOf( "a38.a.a.a.a.test.UnitTester" ) < 16;

        assert testsRun.indexOf( "a38.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.a.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.b.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.c.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.d.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.a.a.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.a.b.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.a.c.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.b.a.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.b.b.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.c.a.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.a.a.a.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.a.a.b.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.b.a.a.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.a.a.a.functiontest.FT" ) < 32;
        assert testsRun.indexOf( "a38.a.a.a.a.functiontest.FT" ) < 32;

        assert testsRun.indexOf( "a38.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.a.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.b.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.c.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.d.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.a.a.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.a.b.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.a.c.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.b.a.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.b.b.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.c.a.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.a.a.a.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.a.a.b.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.b.a.a.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.a.a.a.loadtest.FT" ) < 48;
        assert testsRun.indexOf( "a38.a.a.a.a.loadtest.FT" ) < 48;
        return true;
    }
}
