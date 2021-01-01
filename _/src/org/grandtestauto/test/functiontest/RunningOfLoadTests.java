package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class RunningOfLoadTests extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test42_zip ) );
        gta.runAllTests();
        assert testsRun.size() == 1;
        assert testsRun.contains( "a42.loadtest.LT5" );
        return true;
    }
}
