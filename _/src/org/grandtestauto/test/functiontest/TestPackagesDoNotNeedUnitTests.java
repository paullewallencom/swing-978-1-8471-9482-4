package org.grandtestauto.test.functiontest;

import org.grandtestauto.GrandTestAuto;
import org.grandtestauto.test.Helpers;

import java.io.File;

import jet.testtools.test.org.grandtestauto.Grandtestauto;

/**
 * See the GrandTestAuto test specification.
 */
public class TestPackagesDoNotNeedUnitTests extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test70_zip ) );
        assert gta.runAllTests();
        return true;
    }
}
