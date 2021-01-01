package org.grandtestauto.test.loadtest;

import jet.testtools.*;
import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.test.*;
import org.grandtestauto.test.functiontest.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class Load extends FTBase {
    private Stopwatch creationTimer;
    private Stopwatch runTimer;

    public boolean runTest() throws IOException {
        creationTimer = new Stopwatch();
        runTimer = new Stopwatch();
        //a51:  27 classes in all.
        runForConfiguredPackages( Grandtestauto.test51_zip );
        //a52: 85
        runForConfiguredPackages( Grandtestauto.test52_zip );
        //a60 169
        runForConfiguredPackages( Grandtestauto.test60_zip );
        //a53: 175
        runForConfiguredPackages( Grandtestauto.test53_zip );
        //a54 297
        runForConfiguredPackages( Grandtestauto.test54_zip );
        //a55 451
        runForConfiguredPackages( Grandtestauto.test55_zip );
        //a61 651
        runForConfiguredPackages( Grandtestauto.test61_zip );
        //a62 1653
        runForConfiguredPackages( Grandtestauto.test62_zip );
        //a63 3367
        runForConfiguredPackages( Grandtestauto.test63_zip );
        //a64 5985
        runForConfiguredPackages( Grandtestauto.test64_zip );

        System.out.println( "Total numbers of classes: 27  85" +
                "  169  175  297  451  651  1,653  3,367  5,985" );
        System.out.println( "creation times: " + creationTimer );
        System.out.println( "run times: " + runTimer );

        //Check that the creation times are not growing too fast.
        long ct6 = creationTimer.times().get( 6 );
        long ct7 = creationTimer.times().get( 7 );
        long ct8 = creationTimer.times().get( 8 );
        long ct9 = creationTimer.times().get( 9 );
        assert ct7 < 3 * ct6;
        assert ct8 < 3 * ct7;
        assert ct9 < 3 * ct8;

        //Check that the run times are not growing too fast.
        long rt6 = runTimer.times().get( 6 );
        long rt7 = runTimer.times().get( 7 );
        long rt8 = runTimer.times().get( 8 );
        long rt9 = runTimer.times().get( 9 );
        assert rt7 < 3 * rt6 + 2000;//With such short times, need some leeway.
        assert rt8 < 3 * rt7 + 2000;
        assert rt9 < 3 * rt8;

        //Check that the absolute times are not too great.
        assert creationTimer.max() < 20000;
        assert runTimer.max() < 30000;
        return true;
    }

    /**
     * Expands the given zip of packages and creates and runs a GTA
     * for that package hierarchy, measuring creation and run times.
     */
    private void runForConfiguredPackages( String packageZip )
            throws IOException {
        File zip = new File( packageZip );
        String fileName = Helpers.expandZipAndWriteSettingsFile(
                zip, true, true, true, null, false, true, null );
        creationTimer.start();
        Settings settings = new Settings( fileName );
        GrandTestAuto gta = new GrandTestAuto( settings );
        creationTimer.stop();
        runTimer.start();
        boolean result = gta.runAllTests();
        runTimer.stop();
        //Tests should all have passed.
        assert result;
    }
}
