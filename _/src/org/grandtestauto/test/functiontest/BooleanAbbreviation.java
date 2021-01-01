package org.grandtestauto.test.functiontest;

import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.util.*;

import jet.testtools.test.org.grandtestauto.*;

/**
 * See the GrandTestAuto test specification.
 */
public class BooleanAbbreviation extends FTBase {
    public boolean runTest() throws IOException {
        init();
        //Write a settingsFile file.
        File settingsFile = new File( Helpers.tempDirectory(), "BadSettings.txt" );
        String clsRoot = Helpers.tempDirectory().getAbsolutePath().replace( '\\', '/' );
        Properties props = new Properties();
        props.setProperty( Settings.CLASSES_ROOT, clsRoot );
        props.setProperty( Settings.RUN_UNIT_TESTS, "t" );
        props.setProperty( Settings.RUN_FUNCTION_TESTS, "f" );
        props.setProperty( Settings.RUN_LOAD_TESTS, "T" );
        OutputStream os = new BufferedOutputStream( new FileOutputStream( settingsFile ) );
        props.store( os, getClass().getName() );
        Helpers.expandZipTo( new File( Grandtestauto.test36_zip ), Helpers.tempDirectory() );
        GrandTestAuto.main( new String[] { settingsFile.getAbsolutePath() } );
        assert testsRun.size() == 2;
        assert testsRun.get( 0 ).equals( "a36.test.UnitTester" );
        assert testsRun.get( 1 ).equals( "a36.loadtest.LT" );

        return true;
    }
}
