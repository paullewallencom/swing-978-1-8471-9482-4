package org.grandtestauto.distributed;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The details needed by a <code>TestAgent</code>, read from a properties file.
 *
 * @author Tim Lavers
 */
public class TestAgentSettings {

    /**
     * Key in the settings file for the user directory for this test agent.
     */
    public static final String BASE_DIR = "BASE_DIR";

    /**
     * Key in the settings file for the directory in which the classes to be tested are found
     */
    public static final String CLASSES_ROOT = "CLASSES_ROOT";

    /**
     * Key in the settings file for the name by which this test agent is identified.
     */
    public static final String NAME = "NAME";

    /**
     * Settings key for the maximum grade of test package that this agent can run.
     */
    public static final String MAXIMUM_GRADE = "MAXIMUM_GRADE";

    /**
     * Key in the settings file for the address of the distributor.
     */
    public static final String SERVER_ADDRESS = "SERVER_ADDRESS";

    /**
     * Key in the settings file for the server port.
     */
    public static final String SERVER_PORT = "SERVER_PORT";

    /**
     * Key in the settings file for a list of JVM parameter switches,
     * for example "-server -Xmx956M -Dswing.aatext=true -DLogFine=true -Dinf=true"
     */
    public static final String JVM_OPTIONS_LIST = "JVM_OPTIONS_LIST";

    private File baseDir;
    private File classesRoot;
    private String name;
    private String jvmOptionsList = "";
    private int maximumGrade;
    private InetAddress serverAddress;
    private int serverPort;

    public TestAgentSettings( String settingsFileName ) throws IOException {
        Properties properties = new Properties();
        File settingsFile = new File( settingsFileName );
        InputStream is = new BufferedInputStream( new FileInputStream( settingsFile ) );
        properties.load( is );
        baseDir = getDirectory( properties, BASE_DIR );
        classesRoot = getDirectory( properties, CLASSES_ROOT );
        name = properties.getProperty( NAME );
        if (properties.containsKey( JVM_OPTIONS_LIST )) {
            jvmOptionsList = properties.getProperty( JVM_OPTIONS_LIST );
        }
        serverAddress = InetAddress.getByName( properties.getProperty( SERVER_ADDRESS ) );
        serverPort = Integer.parseInt( properties.getProperty( SERVER_PORT ) );
        try {
            maximumGrade = Integer.parseInt( properties.getProperty( MAXIMUM_GRADE ) );
        } catch (NumberFormatException e) {
            maximumGrade = 0;
        }
    }

    public File baseDir() {
        return baseDir;
    }

    public File classesRoot() {
        return classesRoot;
    }

    public String name() {
        return name;
    }

    public String jvmOptionsList() {
        return jvmOptionsList;
    }

    public int maximumGrade() {
        return maximumGrade;
    }

    public InetAddress serverAddress() {
        return serverAddress;
    }

    public int serverPort() {
        return serverPort;
    }

    private File getDirectory( Properties properties, String key ) {
        File dir = new File( properties.getProperty( key ) );
        dir.mkdirs();
        assert dir.exists();
        return dir;
    }
}
