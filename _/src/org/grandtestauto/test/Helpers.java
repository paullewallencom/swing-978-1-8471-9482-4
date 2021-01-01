package org.grandtestauto.test;

import jet.testtools.*;
import org.grandtestauto.*;
import org.grandtestauto.util.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Helper methods for the tests.
 *
 * @author Tim Lavers
 */
public class Helpers {

    public static String NL = System.getProperty( "line.separator" );

    public static void assertEqual( Object o1, Object o2 ) {
        if (!o1.equals( o2 )) {
            System.out.println( " First Object: " + o1 );
            System.out.println( "Second Object: " + o2 );
            assert false : "Objects differ as above.";
        }
    }

    public static void assertEqual( int i, int j ) {
        if (i != j) {
            System.out.println( " First int: " + i );
            System.out.println( "Second int: " + j );
        }
    }

    /**
     * Writes a file called "TestSettings.txt" into the temp directory
     * that contains the given settings.
     *
     * @return the name of the file
     */
    static String writeSettingsFile( File classRootSetting ) {
        return writeSettingsFile( classRootSetting, true, true, true );
    }

    static String writeSettingsFile( File classRootSetting, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests ) {
        return writeSettingsFile( classRootSetting, runUnitTests, runFunctionTests, runLoadTests, null );
    }

    public static File defaultLogFile() {
        return new File( tempDirectory(), "GTATestLog.txt" );
    }

    public static String logFileContents() {
        return Files.contents( defaultLogFile() );
    }

    public static boolean overallUnitTestResultInLogFile() {
        return overallTestResultFromLog( Messages.OPK_OVERALL_UNIT_TEST_RESULT );
    }

    public static boolean overallFunctionTestResultInLogFile() {
        return overallTestResultFromLog( Messages.OPK_OVERALL_FUNCTION_TEST_RESULT );
    }

    public static boolean overallLoadTestResultInLogFile() {
        return overallTestResultFromLog( Messages.OPK_OVERALL_LOAD_TEST_RESULT );
    }

    static boolean overallTestResultFromLog( String key ) {
        String log = logFileContents();
        //First look for true.
        String pf = Messages.passOrFail( true );
        if (log.contains( Messages.message( key, pf ) )) return true;
        pf = Messages.passOrFail( false );
        //Now look for false.
        if (log.contains( Messages.message( key, pf ) )) return false;
        //Neither. This is an error.
        assert false : "Overall result not written.";
        return false;
    }

    static String writeSettingsFile( File classRootSetting, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackageName ) {
        return writeSettingsFile( classRootSetting, runUnitTests, runFunctionTests, runLoadTests, initialPackageName, false );
    }

    static String writeSettingsFile( File classRootSetting, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackageName, boolean logToConsole ) {
        return writeSettingsFile( classRootSetting, runUnitTests, runFunctionTests, runLoadTests, initialPackageName, null, null, logToConsole, true, defaultLogFile().getAbsolutePath(), null, null, null, null, null, null, null );
    }

    public static String writeSettingsFile( File classRootSetting,
                                            boolean runUnitTests,
                                            boolean runFunctionTests,
                                            boolean runLoadTests,
                                            String initialPackageName,
                                            String finalPackageName,
                                            String singlePackageName,
                                            boolean logToConsole,
                                            boolean logToFile,
                                            String logFileName,
                                            Boolean failFast,
                                            String initialTestWithinPackage,
                                            String finalTestWithinPackage,
                                            String singleTestWithinSinglePackage,
                                            String initialMethod,
                                            String finalMethod,
                                            String singleMethod ) {
        return writeSettingsFile( classRootSetting, runUnitTests, runFunctionTests, runLoadTests,
                initialPackageName, finalPackageName, singlePackageName,
                logToConsole, logToFile, logFileName, failFast,
                initialTestWithinPackage, finalTestWithinPackage, singleTestWithinSinglePackage,
                initialMethod, finalMethod, singleMethod, null );
    }

    public static String writeSettingsFile( File classRootSetting,
                                            boolean runUnitTests,
                                            boolean runFunctionTests,
                                            boolean runLoadTests,
                                            String initialPackageName,
                                            String finalPackageName,
                                            String singlePackageName,
                                            boolean logToConsole,
                                            boolean logToFile,
                                            String logFileName,
                                            Boolean failFast,
                                            String initialTestWithinPackage,
                                            String finalTestWithinPackage,
                                            String singleTestWithinSinglePackage,
                                            String initialMethod,
                                            String finalMethod,
                                            String singleMethod,
                                            String collatedResultsFileName ) {
        String result = null;
        try {
            String clsRoot = classRootSetting.getPath();
            //For windows need to replace '\' in clsRoot by '/' else
            //the settings file won't be parsed properly.
            clsRoot = clsRoot.replace( '\\', '/' );
            File f = new File( tempDirectory(), "TestSettings.txt" );
            result = f.getPath();
            Properties props = new Properties();
            props.setProperty( Settings.CLASSES_ROOT, clsRoot );
            props.setProperty( Settings.LOG_TO_FILE, "true" );
            if (logFileName != null) {
                props.setProperty( Settings.LOG_FILE_NAME, logFileName );
            }
            props.setProperty( Settings.LOG_TO_FILE, "" + logToFile );
            props.setProperty( Settings.LOG_TO_CONSOLE, "" + logToConsole );
            props.setProperty( Settings.RUN_UNIT_TESTS, "" + runUnitTests );
            props.setProperty( Settings.RUN_FUNCTION_TESTS, "" + runFunctionTests );
            props.setProperty( Settings.RUN_LOAD_TESTS, "" + runLoadTests );
            if (initialPackageName != null) {
                props.setProperty( Settings.INITIAL_PACKAGE, initialPackageName );
            }
            if (finalPackageName != null) {
                props.setProperty( Settings.FINAL_PACKAGE, finalPackageName );
            }
            if (singlePackageName != null) {
                props.setProperty( Settings.SINGLE_PACKAGE, singlePackageName );
            }
            if (failFast != null) {
                props.setProperty( Settings.STOP_AT_FIRST_FAILURE, failFast.toString() );
            }
            if (initialTestWithinPackage != null) {
                props.setProperty( Settings.INITIAL_CLASS_WITHIN_SINGLE_PACKAGE, initialTestWithinPackage );
            }
            if (finalTestWithinPackage != null) {
                props.setProperty( Settings.FINAL_CLASS_WITHIN_SINGLE_PACKAGE, finalTestWithinPackage );
            }
            if (singleTestWithinSinglePackage != null) {
                props.setProperty( Settings.SINGLE_CLASS_WITHIN_SINGLE_PACKAGE, singleTestWithinSinglePackage );
            }
            if (initialMethod != null) {
                props.setProperty( Settings.INITIAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, initialMethod );
            }
            if (finalMethod != null) {
                props.setProperty( Settings.FINAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, finalMethod );
            }
            if (singleMethod != null) {
                props.setProperty( Settings.SINGLE_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, singleMethod );
            }
            if (collatedResultsFileName != null) {
                props.setProperty( Settings.COLLATED_RESULTS_FILE_NAME, collatedResultsFileName );
            }
            OutputStream os = new BufferedOutputStream( new FileOutputStream( f ) );
            props.store( os, "Test properties" );
        } catch (IOException ioe) {
            ioe.printStackTrace();
            assert false : "Could not write settings file";
        }
        return result;
    }

    /**
     * A directory for use by the tests.
     */
    public static File tempDirectory() {
        File f = new File( System.getProperty( "user.dir" ), "testtemp" );
        f.mkdirs();
        assert f.exists();
        assert f.isDirectory();
        return f;
    }

    /**
     * Cleans the temp directory.
     */
    public static void cleanTempDirectory() {
        clean( tempDirectory(), true );
    }

    /**
     * Deletes all files from the given directory.
     */
    public static void clean( File dir, boolean recursive ) {
        assert dir.isDirectory();
        assert dir.exists();
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory() && recursive) {
                clean( f, recursive );
                f.delete();
            } else {
                f.delete();
            }
        }
    }

    /**
     * Expands the given archive into the given directory.
     */
    public static void expandZipTo( File zip, File destination ) {
        try {
            ZipFile z = new ZipFile( zip );
            for (Enumeration e = z.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                String name = entry.getName();
                File f = new File( destination, name );
                if (entry.isDirectory()) {
                    f.mkdirs();
                } else {
                    BufferedInputStream input = new BufferedInputStream( z.getInputStream( entry ) );
                    BufferedOutputStream os = new BufferedOutputStream( new FileOutputStream( f ) );
                    byte[] buff = new byte[1024];
                    int r = 0;
                    while (r > -1) {
                        os.write( buff, 0, r );
                        r = input.read( buff, 0, buff.length );
                    }
                    os.close();
                    input.close();
                }
            }
            z.close();
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Could not expand " + zip.getPath() + " to " + destination.getPath();
        }
    }

    /**
     * The root directory for configured test data.
     */
    static File testDataRoot() {
        File userDir = new File( System.getProperty( "user.dir" ) );
        return new File( userDir, "testdata" );
    }

    /**
     * The file into which test data for the given class are stored.
     */
    static File testDataDir( Class clazz ) {
        String relName = clazz.getPackage().getName();
        relName = relName.replace( '.', File.separatorChar );
        //The relname ends with "\test" which must be stripped off.
        relName = relName.substring( 0, relName.length() - 5 );
        return new File( testDataRoot(), relName );
    }

    /**
     * Clean the temp dir, expand the appropriate zip there,
     * write a settings file, and create a GrandTestAuto that
     * will use the settings file and test the unzipped package hierarchy.
     */
    public static GrandTestAuto setupForZip( Class testClass, String zipName ) {
        File zip = new File( Helpers.testDataDir( testClass ), zipName + ".zip" );
        return setupForZip( zip );
    }

    public static GrandTestAuto setupForZip( String zipName ) {
        return setupForZip( new File( zipName ) );
    }

    public static GrandTestAuto setupForZip( File zip ) {
        return setupForZip( zip, true, true, true );
    }

    public static GrandTestAuto setupForZip( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests ) {
        return setupForZip( zip, runUnitTests, runFunctionTests, runLoadTests, null, null );
    }

    public static GrandTestAuto setupForZipWithFailFast( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests ) {
        return setupForZip( zip, runUnitTests, runFunctionTests, runLoadTests, null, null, null, false, true, defaultLogFile().getPath(), Boolean.TRUE, null, null, null, null, null, null );
    }

    public static GrandTestAuto setupForZip( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackage, String finalPackage ) {
        return setupForZip( zip, runUnitTests, runFunctionTests, runLoadTests, initialPackage, finalPackage, null, false, true, defaultLogFile().getPath(), null, null, null, null, null, null, null );
    }

    public static GrandTestAuto setupForZip( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackage, boolean logToConsole, boolean logToFile, String logFileName ) {
        return setupForZip( zip, runUnitTests, runFunctionTests, runLoadTests, initialPackage, null, null, logToConsole, logToFile, logFileName, null, null, null, null, null, null, null );
    }

    public static GrandTestAuto setupForZip( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackage, String finalPackage, String singlePackage, boolean logToConsole, boolean logToFile, String logFileName, Boolean failFast, String initialTestWithinPackage, String finalTestWithinPackage, String singleTestWithinSinglePackage, String initialTestMethod, String finalTestMethod, String singleTestMethod ) {
        cleanTempDirectory();
        String settingsFileName = expandZipAndWriteSettingsFile( zip, runUnitTests, runFunctionTests, runLoadTests, initialPackage, finalPackage, singlePackage, logToConsole, logToFile, logFileName, failFast, initialTestWithinPackage, finalTestWithinPackage, singleTestWithinSinglePackage, initialTestMethod, finalTestMethod, singleTestMethod );
        Settings settings;
        try {
            settings = new Settings( settingsFileName );
            return new GrandTestAuto( settings );
        } catch (IOException e) {
            assert false : "Could not create or read settings: " + e;
        }
        return null;
    }

    public static ProjectAnalyser setupProjectAnalyser( File zip ) {
        cleanTempDirectory();
        String settingsFileName = expandZipAndWriteSettingsFile( zip, true, true, true, null, null, null, true, true, null, false, null, null, null, null, null, null );
        Settings settings;
        try {
            settings = new Settings( settingsFileName );
            return new ProjectAnalyser( settings );
        } catch (IOException e) {
            assert false : "Could not create or read settings: " + e;
        }
        return null;
    }

    public static String expandZipAndWriteSettingsFile( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackage, boolean logToConsole ) {
        return expandZipAndWriteSettingsFile( zip, runUnitTests, runFunctionTests, runLoadTests, initialPackage, logToConsole, true, null );
    }

    public static String expandZipAndWriteSettingsFile( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackage, boolean logToConsole, boolean logToFile, String logFileName ) {
        return expandZipAndWriteSettingsFile( zip, runUnitTests, runFunctionTests, runLoadTests, initialPackage, null, null, logToConsole, logToFile, logFileName, null, null, null, null, null, null, null );
    }

    public static String expandZipAndWriteSettingsFile( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackage, String finalPackage, String singlePackage, boolean logToConsole, boolean logToFile, String logFileName, Boolean failFast, String initialTestWithinPackage, String finalTestWithinPackage, String singleTestWithinSinglePackage, String initialMethod, String finalMethod, String singleMethod ) {
        expandZipTo( zip, tempDirectory() );
        //Write the settings file into the temp dir.
        return writeSettingsFile( Helpers.tempDirectory(), runUnitTests, runFunctionTests, runLoadTests, initialPackage, finalPackage, singlePackage, logToConsole, logToFile, logFileName, failFast, initialTestWithinPackage, finalTestWithinPackage, singleTestWithinSinglePackage, initialMethod, finalMethod, singleMethod );
    }

    public static String runGTAInSeparateJVMAndReadSystemErr( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String initialPackage ) {
        String settingsFileName = expandZipAndWriteSettingsFile( zip, runUnitTests, runFunctionTests, runLoadTests, initialPackage, true );
        return runGTAInSeparateJVM( settingsFileName )[1];
    }

    public static String runGTAForSingleTestInSeparateJVMAndReadSystemErr( File zip, boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, String testName ) {
        expandZipAndWriteSettingsFile( zip, runUnitTests, runFunctionTests, runLoadTests, null, true );
        return runGTAInSeparateJVM( "-run", testName )[0];
    }

    public static String[] runGTAInSeparateJVM( String... args ) {
        StringBuilder cmd = new StringBuilder();
        cmd.append( "java.exe " );
        cmd.append( "-Duser.dir=\"" );
        cmd.append( System.getProperty( "user.dir" ) );
        cmd.append( "\"" );
        cmd.append( " -cp " );
        cmd.append( System.getProperty( "java.class.path" ) );
        cmd.append( " org.grandtestauto.GrandTestAuto" );
        for (String arg : args) {
            cmd.append( " " );
            cmd.append( arg );
        }
        try {
            Process p = Runtime.getRuntime().exec( cmd.toString() );
            return ProcessReader.readProcess( p );
        } catch (IOException e) {
            e.printStackTrace();
            return new String[]{"", ""};//@todo Should really assert fail.
        }
    }
}
