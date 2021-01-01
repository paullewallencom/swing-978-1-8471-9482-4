package org.grandtestauto.test.functiontest;

import org.grandtestauto.test.*;
import org.grandtestauto.*;

import java.io.*;
import java.util.*;

/**
 * See the GrandTestAuto test specification.
 */
public class SettingsReadFromFile extends FTBase {
    public boolean runTest() throws Exception {
        init();
        //Write a dodgy settings file.
        File dodgySettings = new File( Helpers.tempDirectory(), "BadSettings.txt" );
        String clsRoot = Helpers.tempDirectory().getAbsolutePath().replace( '\\', '/' );
        Properties props = new Properties();
        props.setProperty( Settings.CLASSES_ROOT, clsRoot );
        props.setProperty( "NOT_REALLY_A_KEY", "true" );
        OutputStream os = new BufferedOutputStream( new FileOutputStream( dodgySettings ) );
        props.store( os, "SettingsReadFromFile" );

        String sout = Helpers.runGTAInSeparateJVM( dodgySettings.getAbsolutePath() )[0];
        assert sout.contains( Messages.message( Messages.OPK_SETTINGS_FILE_HAS_PROBLEMS,dodgySettings.getAbsolutePath() ));
        File correctedSettingsFile = new File( Helpers.tempDirectory(), "Corrected_BadSettings.txt" );
        assert sout.contains( Messages.message( Messages.OPK_CORRECTED_SETTINGS_FILE_WRITTEN, correctedSettingsFile.getAbsolutePath() ));
        assert sout.contains( Messages.message( Messages.SK_GTA_CONTINUING_WITH_SETTINGS_THAT_COULD_BE_READ ) );
        Settings corrected = new Settings( correctedSettingsFile.getAbsolutePath() );
        assert corrected.unknownKeys().isEmpty();

        //Now when the file does not exist.
        init();
        File nonExistent =new File(Helpers.tempDirectory(), "TestSettings.txt." + System.currentTimeMillis() );
        sout = Helpers.runGTAInSeparateJVM( nonExistent.getAbsolutePath() )[0];
        assert sout.contains( Messages.message( Messages.OPK_SETTINGS_FILE_NOT_FOUND_SO_WRITTEN, nonExistent.getAbsolutePath() ));
        corrected = new Settings( nonExistent.getAbsolutePath() );
        assert corrected.unknownKeys().isEmpty();

        return true;
    }
}
