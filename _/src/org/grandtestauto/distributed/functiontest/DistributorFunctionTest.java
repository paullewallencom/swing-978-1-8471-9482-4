package org.grandtestauto.distributed.functiontest;

import org.grandtestauto.*;
import org.grandtestauto.distributed.*;
import org.grandtestauto.distributed.test.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public abstract class DistributorFunctionTest implements AutoLoadTest {

    private Map<String, TestAgent> nameToAgent = new HashMap<String, TestAgent>();
    private static final String COLLATED_RESULTS_FILE_RELATIVE_NAME = "CollatedResults.txt";
    static Map<String, File> agentNameToBaseDir = new HashMap<String, File>();

    public TestAgent startTestAgent( String name ) throws Exception {
        TestBase tb = new TestBase( false, Settings.DEFAULT_DISTRIBUTOR_PORT );
        tb.init( name );
        TestAgent testAgent = new TestAgent( tb.tas );
        nameToAgent.put( name, testAgent );
        return testAgent;
    }

    protected DistributorFunctionTest() {
        init();
    }

    protected void init() {
        Helpers.cleanTempDirectory();
    }

    protected GTADistributor createDistributorForArchive( String configuredZipName ) throws Exception {
        String settingsFileName = Helpers.expandZipAndWriteSettingsFile( new File( configuredZipName ), true, true, true, null, false );
        Settings settings = new Settings( settingsFileName );
        return new GTADistributor( settings );
    }

    public static Process startTestAgentInSeparateJVM( String testAgentName ) throws Exception {
        return startTestAgentInSeparateJVM( testAgentName , null );
    }

    public static Process startTestAgentInSeparateJVM( String testAgentName, Integer level ) throws Exception {
        //Use a TestBase to write the agent settings.
        TestBase tb = new TestBase( false, Settings.DEFAULT_DISTRIBUTOR_PORT );
        tb.agentLevel = level;
        tb.init( testAgentName );
        agentNameToBaseDir.put( testAgentName, tb.baseDir );
        return createDistributorOrTestAgentProcess( false, tb.settingsFilePath );
    }

    public static Process startDistributorInSeparateJVM( File zip ) throws Exception {
        File classesRoot = TestBase.classesRoot();
        Helpers.expandZipTo( zip, classesRoot );
        //Write the settings file into the temp dir.
        String settingsFileName = Helpers.writeSettingsFile( classesRoot, true, true, true, null, null, null, false, true, "GTADistributorAnalysis.txt", false, null, null, null, null, null, null );
        return createDistributorOrTestAgentProcess( true, settingsFileName );
    }

    public static String collatedResultsFileName(  ) {
        File collatedResultsFile = new File( System.getProperty( "user.dir" ), COLLATED_RESULTS_FILE_RELATIVE_NAME );
        return collatedResultsFile.getAbsolutePath().replaceAll( "\\\\", "/");
    }

    private static Process createDistributorOrTestAgentProcess( boolean trueForDistributor, String settingsFileName ) throws IOException {
        StringBuilder cmd = new StringBuilder();
        cmd.append( "java.exe " );
        cmd.append( "-Duser.dir=\"" );
        cmd.append( System.getProperty( "user.dir" ) );
        cmd.append( "\"" );
        cmd.append( " -cp \"" );
        //Prepend the classes root to the classpath, for a distributor.
        if (trueForDistributor) {
            cmd.append( TestBase.classesRoot().getAbsolutePath( ));
            cmd.append( ";" );
        }
        cmd.append( System.getProperty( "java.class.path" ) );
        cmd.append( "\" org.grandtestauto.distributed." );
        if (trueForDistributor) {
            cmd.append( "GTADistributor" );
        } else {
            cmd.append( "TestAgent" );
        }
        cmd.append( " " );
        cmd.append( settingsFileName );
        return Runtime.getRuntime().exec( cmd.toString() );
    }
}
