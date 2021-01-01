package org.grandtestauto.distributed.test;

import org.grandtestauto.distributed.*;

import java.rmi.*;
import java.rmi.server.*;

/**
 * Part of the test harness for <code>TestAgent</code>.
 *
 * @author Tim Lavers
 */
public class DummyDistributor extends UnicastRemoteObject implements Distributor {

    private TaskSummary detailsToReturn;
    private String reportedSout;
    private String reportedSerr;
    private String reportedResultsFileContents;
    private Exception exceptionToThrowDuringLookup;
    AgentDetails requestClientDetails;

    public DummyDistributor() throws RemoteException {
    }

    public TaskSummary requestWork( AgentDetails clientDetails ) throws RemoteException {
        if (exceptionToThrowDuringLookup != null) {
            throw new RemoteException( "Wrapper RemoteException", exceptionToThrowDuringLookup );
        }
        requestClientDetails = clientDetails;
        return detailsToReturn;
    }

    public void reportResult( TaskResult result ) {
        reportedSout = result.sout();
        reportedSerr = result.serr();
        reportedResultsFileContents = result.resultsFileContents();
        detailsToReturn = null;//So that the test agent doesn't just keep doing the same work.
    }

    void setSummaryToReturn( TaskSummary td ) {
        detailsToReturn = td;
    }

    void setExceptionToThrowDuringLookup( Exception e ) {
        exceptionToThrowDuringLookup = e;
    }

    String reportedSout() {
        return reportedSout;
    }

    String reportedSerr() {
        return reportedSerr;
    }

    String reportedResultsFileContents() {
        return reportedResultsFileContents;
    }
}
