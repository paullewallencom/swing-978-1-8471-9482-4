package org.grandtestauto.distributed.test;

import jet.testtools.*;
import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.distributed.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class GTADistributorTest {

    private GTADistributor distributor;
    public Settings settings;
    public String settingsFileName;

    public boolean constructorTest() throws Exception {
        init( Grandtestauto.test1_zip, true, true, true );
        assert canLookupDistributor();
        Set<String> packagesReceived = new HashSet<String>();
        requestAndPerformWork( packagesReceived, "86" );
        requestAndPerformWork( packagesReceived, "86" );
        requestAndPerformWork( packagesReceived, "86" );
        assert canLookupDistributor();
        requestAndPerformWork( packagesReceived, "86" );
        assert canLookupDistributor();
        requestAndPerformWork( packagesReceived, "86" );
        assert packagesReceived.size() == 5 : packagesReceived;
        assert packagesReceived.contains( "a1.b.e.g" );
        assert packagesReceived.contains( "a1.b" );
        assert packagesReceived.contains( "a1.c.h" );
        assert packagesReceived.contains( "a1.d" );
        assert packagesReceived.contains( "a1" );
        assert distributorShutsDown();

        cleanup();
        return true;
    }

    public boolean requestWorkTest() throws Exception {
        //Package a91 has 4 subpackages, a1, a2, a3, a4.
        //The grade of ai is i.
        init( Grandtestauto.test91_zip, true, true, true );
        //Check that jobs are allocated in decreasing order of difficulty.
        AgentDetails agent = new AgentDetails( "Agent4", 4 );
        TaskSummary task = distributor.requestWork( agent );
        assert task.packageName().equals( "a91.g4" );

        agent = new AgentDetails( "Agent3", 3 );
        task = distributor.requestWork( agent );
        assert task.packageName().equals( "a91.g3" ) : task;

        agent = new AgentDetails( "Agent2", 2 );
        task = distributor.requestWork( agent );
        assert task.packageName().equals( "a91.g2" ) : task;

        agent = new AgentDetails( "Agent1", 1 );
        task = distributor.requestWork( agent );
        assert task.packageName().equals( "a91.g1" );

        cleanup();
        return true;
    }

    public boolean requestWork2Test() throws Exception {
        //Package a93 has 4 subpackages, g1, g2, g3, g4.
        //The grade of ai is i.
        //The complication is that g2 and g4 can only be run by "Agent99".
        init( Grandtestauto.test93_zip, true, true, true );
        //Check that jobs are allocated in decreasing order of difficulty.
        AgentDetails agent86 = new AgentDetails( "Agent86", 10 );
        AgentDetails agent99 = new AgentDetails( "Agent99", 10 );

        TaskSummary task = distributor.requestWork( agent86 );
        assert task.packageName().equals( "a93.g3" ) : task;
        distributor.reportResult( new TR( "Agent86", task ) );

        task = distributor.requestWork( agent99 );
        assert task.packageName().equals( "a93.g4" ) : task;
        distributor.reportResult( new TR( "Agent99", task ) );

        task = distributor.requestWork( agent86 );
        assert task.packageName().equals( "a93.g1" );
        distributor.reportResult( new TR( "Agent86", task ) );

        task = distributor.requestWork( agent86 );
        assert task == null;

        task = distributor.requestWork( agent99 );
        assert task.packageName().equals( "a93.g2" ) : task;
        distributor.reportResult( new TR( "Agent99", task ) );

        cleanup();
        return true;
    }

    public boolean stopTest() throws Exception {
        init( Grandtestauto.test91_zip, true, true, true );
        distributor.stop();
        assert distributorShutsDown();
        cleanup();
        return true;
    }

    public boolean mainTest() throws Exception {
        //init creates a settings file and a distributor.
        init( Grandtestauto.test91_zip, true, true, true );
        //Shut down the distributor.
        distributor.stop();
        assert distributorShutsDown();
        //Now we can test main.
        GTADistributor.main( new String[]{settingsFileName} );
        Distributor d = (Distributor) Naming.lookup( distributorLookup() );
        Set<String> packagesReceived = new HashSet<String>();
        requestAndPerformWork( d, packagesReceived, "86" );
        requestAndPerformWork( d, packagesReceived, "86" );
        requestAndPerformWork( d, packagesReceived, "86" );
        requestAndPerformWork( d, packagesReceived, "86" );
        assert distributorShutsDown();
        cleanup();
        return true;
    }

    public boolean reportResultTest() throws Exception {
        //Check that the results are logged as they come in.
        //Check that when all of the results are in, the distributor shuts down.
        //Check that when all of the results are in,
        //the results are collated and written to file.
        init( Grandtestauto.test91_zip, true, true, true );
        AgentDetails agent = new AgentDetails( "Agent4", 4 );
        TaskSummary task = distributor.requestWork( agent );
        TR tr = new TR( "Agent4", task );
        tr.resultsFileContents = "TR4 RFC";
        assert !logFileContents().contains( tr.toString() );
        distributor.reportResult( tr );
        assert logFileContents().contains( tr.toString() );

        agent = new AgentDetails( "Agent2", 2 );
        task = distributor.requestWork( agent );
        tr = new TR( "Agent2", task );
        tr.resultsFileContents = "TR2 RFC";
        assert !logFileContents().contains( tr.toString() );
        distributor.reportResult( tr );
        assert logFileContents().contains( tr.toString() );

        agent = new AgentDetails( "Agent3", 3 );
        task = distributor.requestWork( agent );
        tr = new TR( "Agent3", task );
        tr.resultsFileContents = "TR3 RFC";
        assert !logFileContents().contains( tr.toString() );
        distributor.reportResult( tr );
        assert logFileContents().contains( tr.toString() );

        assert !collatedResultsFile().exists();
        agent = new AgentDetails( "Agent1", 1 );
        task = distributor.requestWork( agent );
        tr = new TR( "Agent1", task );
        tr.resultsFileContents = "TR1 RFC";
        assert !logFileContents().contains( tr.toString() );
        distributor.reportResult( tr );
        assert logFileContents().contains( tr.toString() );
        Waiting.ItHappened ih = new Waiting.ItHappened() {
            public boolean itHappened() {
                return collatedResultsFile().exists();
            }
        };
        Waiting.waitFor( ih, 2 * GTADistributor.PAUSE_TIME );
        assert collatedResultsFile().exists() : "Should exist: '" + collatedResultsFile().getAbsolutePath() + "'";
        String expected = "TR1 RFC" + Helpers.NL +
                "TR2 RFC" + Helpers.NL +
                "TR3 RFC" + Helpers.NL +
                "TR4 RFC" + Helpers.NL;
        assert collatedResults().equals( expected );
        assert distributorShutsDown();

        cleanup();
        return true;
    }

    private void cleanup() {
        //In some tests, this is not necessary, but it does no harm.
        distributor.stop();
        Waiting.pause( GTADistributor.PAUSE_TIME + 100 );
        try {
            UnicastRemoteObject.unexportObject( distributor, true );
        } catch (NoSuchObjectException e) {
            //Likely to happen in some tests.
        }
    }

    private boolean canLookupDistributor() throws Exception {
        String distributorLookup = distributorLookup();
        Remote remote = null;
        try {
            remote = Naming.lookup( distributorLookup );
        } catch (NotBoundException e) {
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            assert false : "Malformed URL: '" + distributorLookup + "'";
        } catch (RemoteException e) {
            e.printStackTrace();
            assert false : "Remote exception as shown.";
        }
        return remote != null;
    }

    private String distributorLookup() {
        return "//127.0.0.1:" + Settings.DEFAULT_DISTRIBUTOR_PORT + "/" + Distributor.SERVICE_NAME;
    }

    private boolean distributorShutsDown() throws Exception {
        Waiting.pause( GTADistributor.PAUSE_TIME + 100 );
        return !canLookupDistributor();
    }

    private void requestAndPerformWork( Set<String> packagesReceived, String agentName ) throws RemoteException {
        requestAndPerformWork( distributor, packagesReceived, agentName );
    }

    private void requestAndPerformWork( Distributor distributor, Set<String> packagesReceived, String agentName ) throws RemoteException {
        AgentDetails ad = new AgentDetails( agentName, 100 );
        TaskSummary td = distributor.requestWork( ad );

        packagesReceived.add( td.packageName() );
        distributor.reportResult( tr( agentName, td ) );
    }

    private void init( String configuredZipName, boolean runUT, boolean runFT, boolean runLT ) throws Exception {
        Helpers.cleanTempDirectory();
        Helpers.expandZipTo( new File( configuredZipName ), Helpers.tempDirectory() );
        String collatedResultsFileName = "/testtemp/GTADResults.txt";
        settingsFileName = Helpers.writeSettingsFile( Helpers.tempDirectory(), runUT, runFT, runLT,
                null, null, null, false, true, null, false, null, null, null, null, null, null, collatedResultsFileName );
        settings = new Settings( settingsFileName );
        distributor = new GTADistributor( settings );
    }

    private TaskResult tr( String agentName, TaskSummary td ) {
        return new TR( agentName, td );
    }

    private String logFileContents() {
        File logsDir = new File( System.getProperty( "user.dir" ), "logs" );
        File logFile = new File( logsDir, "GTADistributor.log.0" );
        return Files.contents( logFile );
    }

    private String collatedResults() {
        return Files.contents( collatedResultsFile() );
    }

    private File collatedResultsFile() {
        return new File( Helpers.tempDirectory(), "GTADResults.txt" );
    }

}

class TR implements TaskResult, Serializable {
    String agentName;
    TaskSummary td;
    String serr, sout, resultsFileContents;

    TR( String agentName, TaskSummary td ) {
        this.agentName = agentName;
        this.td = td;
    }

    public String testAgentName() {
        return agentName;
    }

    public TaskSummary taskSummary() {
        return td;
    }

    public String serr() {
        return serr == null ? "" : serr;
    }

    public String sout() {
        return sout == null ? "" : sout;
    }

    public String resultsFileContents() {
        return resultsFileContents == null ? "" : resultsFileContents;
    }

    public int compareTo( TaskResult o ) {
        return agentName.compareTo( o.testAgentName() );
    }

    @Override
    public String toString() {
        return "TR_" + agentName;
    }
}