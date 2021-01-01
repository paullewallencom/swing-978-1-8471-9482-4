package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class MissingUnitTestsRecorded extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test45_zip ) );
        assert !gta.runAllTests();
        assert !Helpers.overallUnitTestResultInLogFile();
        assert Helpers.overallFunctionTestResultInLogFile();
        assert Helpers.overallLoadTestResultInLogFile();
        String errorMessage = "The following packages are not unit-tested:" + Helpers.NL + "a45.a" + Helpers.NL + "a45.a.b";
        assert Helpers.logFileContents().contains( errorMessage );
        return true;
    }
}
