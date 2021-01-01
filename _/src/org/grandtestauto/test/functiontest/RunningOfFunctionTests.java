package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class RunningOfFunctionTests extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test41_zip ) );
        gta.runAllTests();
        assert testsRun.size() == 1;
        assert testsRun.contains( "a41.functiontest.FT5" );
        return true;
    }
}
