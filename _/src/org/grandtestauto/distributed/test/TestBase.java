package org.grandtestauto.distributed.test;

import org.grandtestauto.distributed.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class TestBase {

    private boolean cleanTempDirOnInit = true;
    public TestAgentSettings tas;
    public File baseDir;
    public String settingsFileName = "TestSettings.txt";
    public String name = "86";
    public InetAddress serverAddress;
    public int serverPort = 16939;
    public String settingsFilePath;
    public Integer agentLevel;

    public static File classesRoot() {
        return new File( Helpers.tempDirectory(), "classesRoot" );
    }

    public static File testDataDir() {
        return new File( Helpers.tempDirectory(), "testDataDir" );
    }

    public TestBase() {
    }

    public TestBase( boolean cleanTempDirOnInit, int serverPort ) {
        this.cleanTempDirOnInit = cleanTempDirOnInit;
        this.serverPort = serverPort;
    }

    public void init() throws Exception {
        init( settingsFileName, "DefaultTestAgent" );
    }

    public void init( String agentName ) throws Exception {
        init( settingsFileName, agentName );
    }

    public void init( String relativeNameForSettingsFile, String agentName ) throws Exception {
        if (cleanTempDirOnInit) {
            Helpers.cleanTempDirectory();
        }
        File agentDir = new File( Helpers.tempDirectory(), agentName );
        agentDir.mkdirs();
        baseDir = new File( agentDir, "baseDir" );
        serverAddress = InetAddress.getLocalHost();
        Properties props = new Properties();
        props.put( TestAgentSettings.BASE_DIR, baseDir.getAbsolutePath() );
        props.put( TestAgentSettings.CLASSES_ROOT, classesRoot().getAbsolutePath() );
        if (agentName == null) {
            props.put( TestAgentSettings.NAME, name );
        } else {
            props.put( TestAgentSettings.NAME, agentName );
        }
        if (agentLevel != null) {
            props.put( TestAgentSettings.MAXIMUM_GRADE, "" + agentLevel );
        }
        props.put( TestAgentSettings.SERVER_ADDRESS, serverAddress.getHostAddress() );
        props.put( TestAgentSettings.SERVER_PORT, "" + serverPort );
        File settingsFile = new File( agentDir, relativeNameForSettingsFile );
        OutputStream is = new BufferedOutputStream( new FileOutputStream( settingsFile ) );
        props.store( is, "Written in test" );
        settingsFilePath = settingsFile.getAbsolutePath();
        tas = new TestAgentSettings( settingsFilePath );
    }
}