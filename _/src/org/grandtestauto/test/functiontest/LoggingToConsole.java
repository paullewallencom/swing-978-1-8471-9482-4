package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.test.*;
import org.grandtestauto.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class LoggingToConsole extends FTBase {
    public boolean runTest() {
        //Run GTA for a configured package
        //in a separate JVM and read the console output.
        String sout = Helpers.runGTAInSeparateJVMAndReadSystemErr(
                new File( Grandtestauto.test36_zip ),
                true, true, true, null );
        //Check that the output contains:
        //...the settings summary
        assert sout.contains( new Settings().summary() ) : sout;
        //...the unit test results
        assert sout.contains( Messages.message(
                Messages.TPK_UNIT_TEST_PACK_RESULTS,
                "a36", Messages.passOrFail( true ) ) );
        //...the function test results
        assert sout.contains( Messages.message(
                Messages.TPK_FUNCTION_TEST_PACK_RESULTS,
                "a36.functiontest", Messages.passOrFail( true ) ) );
        //...the load test results
        assert sout.contains( Messages.message(
                Messages.TPK_LOAD_TEST_PACK_RESULTS,
                "a36.loadtest", Messages.passOrFail( true ) ) );
        //...and the overall result.
        assert sout.contains( Messages.message(
                Messages.OPK_OVERALL_GTA_RESULT,
                Messages.passOrFail( true ) ) );
        return true;
    }
}
