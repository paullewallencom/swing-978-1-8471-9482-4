package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class NamingOfFunctionTestPackages extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test39_zip ), false, true, false );//Function tests only.
        gta.runAllTests();
        assert testsRun.size() == 1;
        assert testsRun.get( 0 ).equals( "a39.functiontest.FT" );
        return true;
    }
}
