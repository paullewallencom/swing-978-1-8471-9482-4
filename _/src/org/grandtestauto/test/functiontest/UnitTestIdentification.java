package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class UnitTestIdentification extends FTBase {
    public boolean runTest() {
        Helpers.setupForZip( new File( Grandtestauto.test46_zip ), true, true, true ).runAllTests();
        String logFileContents = Helpers.logFileContents();
        assert logFileContents.contains( "XTest" );
        assert testsRun.contains( "a46.test.XTest" );
        String msgForYTest = Messages.message( Messages.OPK_COULD_NOT_CREATE_TEST_CLASS, "a46.test.YTest" );
        assert logFileContents.contains( msgForYTest );
        String msgForZTest = Messages.message( Messages.OPK_COULD_NOT_CREATE_TEST_CLASS, "a46.test.ZTest" );
        assert logFileContents.contains( msgForZTest );
        return true;
    }
}
