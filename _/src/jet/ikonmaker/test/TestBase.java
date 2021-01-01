package jet.ikonmaker.test;

import jet.ikonmaker.*;
import jet.testtools.*;

import java.io.*;
import java.util.prefs.*;

/**
 * @author Tim Lavers
 */
public class TestBase {
    File testDataDir= new File( System.getProperty( "user.dir" ), "testdata" );
    protected File storeDir;
    protected File exportDir;
    protected IkonMakerUserStrings us = IkonMakerUserStrings.instance();
    protected Preferences preferences;

    void copyIkonFromTestData( String fileName ) {
        File  ikonFile = new File( fileName );
        try {
            Files.copyTo( ikonFile, storeDir );
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Could not copy ikon, as shown above.";
        }
    }

    void init() {
        //Get the preferences for the IkonMaker, clear them out.
        preferences = Preferences.userNodeForPackage( IkonMaker.class );
        try {
            preferences.clear();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            assert false : "Could not clear preferences, as shown.";
        }
        //Set up the directories.
        File root = Files.cleanedTempDir();
        storeDir = new File( root, "store" );
        storeDir.mkdirs();
        exportDir = new File( root, "export" );
        exportDir.mkdirs();
    }
}