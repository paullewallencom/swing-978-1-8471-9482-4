package org.grandtestauto.distributed;

import java.rmi.*;

/**
 * @author Tim Lavers
 */
public interface Distributor extends Remote {
    static final String SERVICE_NAME = "GTADistributor";

    TaskSummary requestWork( AgentDetails clientDetails ) throws RemoteException;

    void reportResult( TaskResult result ) throws RemoteException;
}
