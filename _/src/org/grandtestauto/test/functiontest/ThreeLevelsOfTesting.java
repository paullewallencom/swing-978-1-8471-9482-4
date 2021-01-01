package org.grandtestauto.test.functiontest;

import static jet.testtools.test.org.grandtestauto.Grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class ThreeLevelsOfTesting extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( test36_zip ) );
        gta.runAllTests();
        assert testsRun.size() == 3;
        assert testsRun.get( 0 ).equals( "a36.test.UnitTester" );
        assert testsRun.get( 1 ).equals( "a36.functiontest.FT" );
        assert testsRun.get( 2 ).equals( "a36.loadtest.LT" );

        return true;
    }
}
