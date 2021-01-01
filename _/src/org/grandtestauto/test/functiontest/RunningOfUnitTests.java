package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class RunningOfUnitTests extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test40_zip ) );
        gta.runAllTests();
        assert testsRun.size() == 2;
        assert testsRun.contains( "a40.c.test.UnitTester" );
        assert testsRun.contains( "a40.d.test.UnitTester" );
        return true;
    }
}
