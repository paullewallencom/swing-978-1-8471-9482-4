package org.grandtestauto.distributed.test;

import jet.testtools.*;
import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.distributed.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.net.*;
import java.rmi.registry.*;
import java.rmi.server.*;

/**
 * @author Tim Lavers
 */
public class TestAgentTest extends TestBase {
    /*
    Things to test....
    1. That the distributor lookup works for getting work
    2. That the taskdetails result of a work request is respected, including null values.
    3. That exceptions in requesting task details are handled smoothly and logged.
    4. That the distributor lookup for reportring results works
    5. That the results are reported correctly
    6. That exceptions in reportring results are handled smoothly and logged.
    7. That exceptions, failures, and JVM deaths in running tests are handled smoothly, logged, and reported.
    8. That the settings are read correctly.
    9. That the internal thread is started in the constructor.
    10. That the stop method works.

     *
     */
    private DummyDistributor distributor;
    private TestAgent agent;
    private Registry registry;

    public void init() throws Exception {
        super.init();
        distributor = new DummyDistributor();
        serverAddress = InetAddress.getLocalHost();
        registry = LocateRegistry.createRegistry( serverPort );
        registry.rebind( Distributor.SERVICE_NAME, distributor );
        agent = new TestAgent( tas );
    }

    private void cleanup() throws Exception {
        agent.stop();
        UnicastRemoteObject.unexportObject( distributor, true );
        UnicastRemoteObject.unexportObject( registry, true );
    }

    public boolean constructorTest() throws Exception {
        init();//Calls the constructor.
        assert testAgentLog().contains( "Will lookup distributor on" ) : testAgentLog();
        Waiting.pause( TestAgent.PAUSE_TIME );
        assert testAgentLog().contains( "INFO: polling for work..." ) : testAgentLog();
        cleanup();
        return true;
    }

    public boolean mainTest() {
        //This is tested well enough in the function tests.
        return true;
    }

    public boolean stopTest() throws Exception {
        init();
        //Check that the internal thread is running...
        assert TestHelper.namesOfActiveThreads().contains( "TestAgentRunner_DefaultTestAgent" );
        agent.stop();
        //...and then finishes.
        boolean threadStopped = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return !TestHelper.namesOfActiveThreads().contains( "TestAgentRunner_DefaultTestAgent" );
            }
        }, 3 * TestAgent.PAUSE_TIME );
        assert threadStopped;

        cleanup();
        return true;
    }

    public boolean lookupWorkTest() throws Exception {
        init();
        waitForAgentToHaveMadeRequest();
        assert distributor.requestClientDetails.name().equals( tas.name() );
        cleanup();
        return true;
    }

    public boolean doWorkTest() throws Exception {
        init();
        //Set up something to be done.
        Helpers.expandZipTo( new File( Grandtestauto.test1_zip ), classesRoot() );
        String classPath = classesRoot().getAbsolutePath();
        TaskSummary td = new TaskSummary( "a1.b", classPath );
        distributor.setSummaryToReturn( td );
        waitForAgentToHaveMadeRequest();
        waitForAgentToHaveReportedResults();
        String pf = Messages.passOrFail( false );
        String expected = Messages.message( Messages.TPK_UNIT_TEST_PACK_RESULTS, "a1.b", pf );
        assert distributor.reportedResultsFileContents().contains( expected );
        cleanup();
        return true;
    }

    public boolean handleLookupExceptionTest() throws Exception {
        init();
        //Set an exception to throw.
        final String message = "Test exception.";
        assert !testAgentLog().contains( message ) : testAgentLog();
        distributor.setExceptionToThrowDuringLookup( new Exception( message ) );
        //Check that the exception message gets logged.
        assert messageIsLogged( message ) : testAgentLog();
        //Now stop the exception from being thrown.
        distributor.setExceptionToThrowDuringLookup( null );
        waitForAgentToHaveMadeRequest();

        cleanup();
        return true;
    }

    public boolean logTest() throws Exception {
        init();
        final String message = "Rachmaninov";
        assert !testAgentLog().contains( message ) : testAgentLog();
        agent.log( message );
        assert messageIsLogged( message ) : testAgentLog();
        cleanup();
        return true;
    }

    private String testAgentLog() {
        File logsDir = new File( baseDir, "logs" );
        File log = new File( logsDir, "TestAgent.log.0" );
        return Files.contents( log );
    }

    private void waitForAgentToHaveMadeRequest() {
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return distributor.requestClientDetails != null;
            }
        }, 12000 );
    }

    private boolean messageIsLogged( final String message ) {
        return Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return testAgentLog().contains( message );
            }
        }, 5000 );
    }

    private void waitForAgentToHaveReportedResults() {
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return distributor.reportedResultsFileContents() != null;
            }
        }, 12000 );
    }
}
