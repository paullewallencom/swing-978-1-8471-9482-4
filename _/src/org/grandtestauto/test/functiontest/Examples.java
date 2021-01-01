package org.grandtestauto.test.functiontest;

import org.grandtestauto.GrandTestAuto;
import org.grandtestauto.test.Helpers;

import java.io.File;

import jet.testtools.test.org.grandtestauto.Grandtestauto;

/**
 * Runs the example packages.
 */
public class Examples extends FTBase {
    public boolean runTest() {
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.example1_zip ) );
        assert gta.runAllTests();
        System.out.println( Helpers.logFileContents() );

        Helpers.setupForZip( new File( Grandtestauto.example2_zip ) );
//        assert gta.runAllTests();
        System.out.println( Helpers.logFileContents() );

        gta = Helpers.setupForZip( new File( Grandtestauto.example3_zip ) );
        assert gta.runAllTests();
        System.out.println( Helpers.logFileContents() );
        return true;
    }
}
