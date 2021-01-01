package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class GTAMain extends FTBase {
    public boolean runTest() {
        //Setup the test package.
        String settingsFileName = Helpers.expandZipAndWriteSettingsFile( new File( Grandtestauto.test36_zip ), true, true, true, null, false, true, Helpers.defaultLogFile().getPath() );
        //First call main with a single argument.
        GrandTestAuto.main( new String[]{settingsFileName} );
        String logFileContents = Helpers.logFileContents();
        assert logFileContents.contains( new Settings().summary() );
        assert logFileContents.contains( Messages.message( Messages.TPK_UNIT_TEST_PACK_RESULTS, "a36", Messages.passOrFail( true ) ) );
        assert logFileContents.contains( Messages.message( Messages.TPK_FUNCTION_TEST_PACK_RESULTS, "a36.functiontest", Messages.passOrFail( true ) ) );
        assert logFileContents.contains( Messages.message( Messages.TPK_LOAD_TEST_PACK_RESULTS, "a36.loadtest", Messages.passOrFail( true ) ) );
        assert logFileContents.contains( Messages.message( Messages.OPK_OVERALL_GTA_RESULT, Messages.passOrFail( true ) ) );

        //Now call main with -run option. As the logging is only to the console,
        //we need to run this in a separate JVM and read in stdout.
        String sout = Helpers.runGTAForSingleTestInSeparateJVMAndReadSystemErr( new File( Grandtestauto.test36_zip ), true, true, true, "a36.functiontest.FT" );
        assert sout.contains( "a36.functiontest.FT passed," ) : "got: '" + sout + "'";
        return true;
    }
}
