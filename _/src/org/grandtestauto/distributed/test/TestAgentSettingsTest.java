package org.grandtestauto.distributed.test;

import jet.testtools.test.org.grandtestauto.distributed.*;
import org.grandtestauto.distributed.*;

/**
 * @author Tim Lavers
 */
public class TestAgentSettingsTest extends TestBase {

    public boolean constructorTest() throws Exception {
        //Really just checking that the name argument is respected.
        init( "TAS1.txt", "Larrabee" );
        assert tas.name().equals( "Larrabee" );
        init( "TAS2.txt", "44" );
        assert tas.name().equals( "44" );
        return true;
    }

    public boolean baseDirTest() throws Exception {
        init( name );
        assert tas.baseDir().equals( baseDir );
        return true;
    }

    public boolean classesRootTest() throws Exception {
        init( name );
        assert tas.classesRoot().equals( classesRoot() );
        return true;
    }

    public boolean jvmOptionsListTest() throws Exception {
        TestAgentSettings tas1 = new TestAgentSettings( Distributed.TestAgent1_properties );
        assert tas1.jvmOptionsList().equals( "" );
        TestAgentSettings tas2 = new TestAgentSettings( Distributed.TestAgent2_properties );
        assert tas2.jvmOptionsList().equals( "-server -Xmx956M -Dswing.aatext=true -DLogFine=true -Dinf=true" );
        TestAgentSettings tas3 = new TestAgentSettings( Distributed.TestAgent3_properties );
        assert tas3.jvmOptionsList().equals( "" );
        return true;
    }

    public boolean maximumGradeTest() throws Exception {
        init( name );
        assert tas.maximumGrade() == 0;

        agentLevel = 99;
        init( name );
        assert tas.maximumGrade() == 99;
        agentLevel = null;
        return true;
    }

    public boolean nameTest() throws Exception {
        init( name );
        assert tas.name().equals( name );
        return true;
    }

    public boolean serverAddressTest() throws Exception {
        init( name );
        assert tas.serverAddress().equals( serverAddress );
        return true;
    }

    public boolean serverPortTest() throws Exception {
        init( name );
        assert tas.serverPort() == serverPort;
        return true;
    }
}