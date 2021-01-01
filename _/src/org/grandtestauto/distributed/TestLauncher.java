package org.grandtestauto.distributed;

import jet.testtools.*;
import org.grandtestauto.*;

import java.io.*;

/**
 * Starts a new JVM that runs GrandTestAuto configured to run
 * just a single package of tests.
 *
 * @author Tim Lavers
 */
public class TestLauncher {

    /**
     * Represents the TestAgent, to which progress can be reported.
     */
    public interface Controller {
        void log( String str );
    }

    public final static String TEST_DATA_KEY = "TestDataRoot";

    private String classPath;
    private File baseDir;
    private String jvmOptionsList;
    private Controller controller;

    public TestLauncher( Controller controller, String classPath, File baseDir, String jvmOptionsList ) {
        assert controller != null;
        assert classPath != null;
        assert baseDir.exists();
        assert jvmOptionsList != null;
        this.controller =controller;
        //The classpath will need to have gta in it. It may already have.
        //However, there is no harm in appending the present application's
        //classpath just to make sure.
        StringBuilder cpBuilder = new StringBuilder( classPath );
        String thisCP = System.getProperty( "java.class.path" );
        for (String cpElement : thisCP.split( ";" )) {
            File location;
            if (cpElement.startsWith( ".." )) {
                File parent = new File( System.getProperty( "user.dir" ) ).getParentFile();
                location = new File( parent, cpElement.substring( 2 ) );
            } else if (cpElement.startsWith( "." )) {
                File parent = new File( System.getProperty( "user.dir" ) );
                location = new File( parent, cpElement.substring( 1 ) );
            } else {
                location = new File( cpElement );
            }
            cpBuilder.append( ";" );
            cpBuilder.append( location.getAbsolutePath() );
        }
        cpBuilder.append( ";" );
        this.classPath = cpBuilder.toString();
        this.baseDir = baseDir;
        this.jvmOptionsList = jvmOptionsList;
        Files.cleanDirectory( baseDir );
    }

    public Process startTestJVM( String packageName, File classesRoot ) throws IOException {
        assert packageName != null;
        assert classesRoot.exists() : "Does not exist: " + classesRoot.getCanonicalPath();
        //Write a GTA Settings file in the base dir.
        //The settings are: the classes dir, the package to run, and the results file.
        StringBuilder gtaSettings = new StringBuilder( Settings.CLASSES_ROOT );
        gtaSettings.append( "=" ).append( classesRoot.getCanonicalPath().replaceAll( "\\\\", "/" ) );
        gtaSettings.append( StringUtils.NL );
        gtaSettings.append( Settings.SINGLE_PACKAGE );
        gtaSettings.append( "=" );
        gtaSettings.append( packageName );
        File resultsFile = new File( baseDir, "GTAResults.txt" );
        gtaSettings.append( StringUtils.NL );
        gtaSettings.append( Settings.LOG_FILE_NAME ).append( "=" ).append( resultsFile.getCanonicalPath().replaceAll( "\\\\", "/" ) );
        gtaSettings.append( StringUtils.NL );
        gtaSettings.append( Settings.LOG_TO_FILE ).append( " = true" ).append( StringUtils.NL );
        gtaSettings.append( Settings.LOG_TO_CONSOLE ).append( " = true" ).append( StringUtils.NL );
        gtaSettings.append( Settings.LESS_VERBOSE ).append( " = true" ).append( StringUtils.NL );
        controller.log( "baseDir is: '" + baseDir.getAbsolutePath() + "'" );
        File settingsFile = new File( baseDir, "GTASettings.txt" );
        Files.writeIntoFile( gtaSettings.toString(), settingsFile );
        //Create the user dir.
        File userDir = new File( baseDir.getAbsoluteFile(), "userDir" );
        assert userDir.mkdir() : "Could not make: " + userDir.getAbsolutePath();
        StringBuilder cmd = new StringBuilder();
        //We are assuming that java is in the path.
        cmd.append( "java.exe " );
        //The list of JVM options.
        cmd.append( " " ).append( jvmOptionsList ).append( " " );
        //The user directory.
        cmd.append( "-Duser.dir=\"" );
        cmd.append( userDir.getCanonicalPath() );
        cmd.append( "\"" );
        //The classpath.
        cmd.append( " -cp \"" );
        //Here we are doing a bit of a hack that allows this Distributed GTA itself to be
        //tested by this system. We have to add a temp dir to the classpath. This tempdir
        //is where configured packages that get run in GTA unit and function tests are put.
        cmd.append( userDir.getAbsolutePath() ).append( File.separator ).append( "testtemp/");
        cmd.append( ";");
        cmd.append( classPath );
        cmd.append( ";");
        cmd.append( "\"" );
        //The main class and its argument, which is the settings file location.
        cmd.append( " org.grandtestauto.GrandTestAuto \"" );
        cmd.append( settingsFile.getAbsolutePath().replaceAll( "\\\\", "/" ) );
        cmd.append( "\"" );
        controller.log( "TestLauncher about to start command '" + cmd+"'");
        //As part of the GTA testing hack mentioned above, we have to create
        //the directories before we kick off the new JVM, otherwise these might
        //be ignored.
        userDir.mkdirs();
        File testtemp = new File( userDir, "testtemp");
        testtemp.mkdirs();
        return Runtime.getRuntime().exec( cmd.toString() );
    }
}
