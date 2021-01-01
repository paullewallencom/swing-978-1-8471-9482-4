package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

/**
 * See the GrandTestAuto test specification.
 */
public class AccessibleClassesRequireUnitTest extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( Grandtestauto.test4_zip );
        //This gta should fail because there is an untested class.
        assert !gta.runAllTests();
        String logFileContents = Helpers.logFileContents();
        String errorMessage = "In a4 the following class is not unit-tested:" + Helpers.NL + "X";
        assert logFileContents.contains( errorMessage );
        return true;
    }
}
