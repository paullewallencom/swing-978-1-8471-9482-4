package org.grandtestauto.distributed;

import org.grandtestauto.*;

import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.util.logging.*;

/**
 * @author Tim Lavers
 */
public class GTADistributor extends UnicastRemoteObject implements Distributor {

    public static final int PAUSE_TIME = 2000;

    private static enum State {
        UNALLOCATED, ALLOCATED, COMPLETED
    }

    private Settings settings;
    private SortedMap<TaskDetails, State> workstates = new TreeMap<TaskDetails, State>();
    private Registry registry;
    private Logger logger;
    private FileHandler fileHandler;
    private SortedSet<TaskResult> unitTestResults = new TreeSet<TaskResult>();
    private boolean stopped = false;

    public static void main( String[] args ) throws Exception {
        Settings settings = new Settings( args[0] );
        new GTADistributor( settings );
    }

    public GTADistributor( Settings settings ) throws IOException {
        this.settings = settings;
        File baseDir = new File( System.getProperty( "user.dir" ) );
        File logsDir = new File( baseDir, "logs" );
        logsDir.mkdirs();
        logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers( false );
        logger.setLevel( Level.ALL );
        fileHandler = new FileHandler( logsDir.getPath() + "/GTADistributor.log", 1024 * 1024, 10, false );
        fileHandler.setFormatter( new ResultsFormatter() );
        logger.addHandler( fileHandler );

        String classpath = System.getProperty( "java.class.path" );
        log( "Classpath is: '" + classpath + "'" );
        ProjectAnalyser pa = new ProjectAnalyser( settings );
        log( "Project analyser created...." );
        for (String utPackageName : pa.unitTestPackages()) {
            workstates.put( new TaskDetails( utPackageName, classpath ), State.UNALLOCATED );
        }
        log( "Unit tests recorded..." );
        for (String ftPackageName : pa.functionTestPackages()) {
            workstates.put( new TaskDetails( ftPackageName, classpath ), State.UNALLOCATED );
        }
        log( "Function tests recorded..." );
        for (String ltPackageName : pa.loadTestPackages()) {
            workstates.put( new TaskDetails( ltPackageName, classpath ), State.UNALLOCATED );
        }
        log( "Load tests recorded..." );
        logState();
        new ShutdownThread().start();
        //Export it to RMI.
        log( "About to export..." );
        try {
            registry = LocateRegistry.createRegistry( settings.distributorPort() );
        } catch (Exception e) {
            registry = LocateRegistry.getRegistry( settings.distributorPort() );
        }
        registry.rebind( Distributor.SERVICE_NAME, this );
        log( "Exported..." );
    }

    public synchronized TaskSummary requestWork( AgentDetails agentDetails ) throws RemoteException {
        log( "Work request from: " + agentDetails );
        //Find the first unallocated task, return it.
        TaskDetails details = null;
        for (TaskDetails td : workstates.keySet()) {
            if (workstates.get( td ).equals( State.UNALLOCATED )) {
                if (td.canRunTests( agentDetails )) {
                    details = td;
                    break;
                }
            }
        }
        TaskSummary result = null;
        if (details != null) {
            workstates.put( details, State.ALLOCATED );
            result = new TaskSummary( details.packageName(), details.classpath() );
        }
        log( "Resturning " + result +" to " +agentDetails );
        logState();
        return result;
    }

    public synchronized void reportResult( TaskResult result ) throws RemoteException {
        //Log it.
        log( "Got result..." );
        log( result.toString() );
        //Set the package workstate.
        TaskDetails toUpdate = null;
        for (TaskDetails td : workstates.keySet()) {
            if (td.packageName().equals( result.taskSummary().packageName() )) {
                toUpdate = td;
                break;
            }
        }
        workstates.put( toUpdate, State.COMPLETED );
        logState();
        //Record the result object.
        unitTestResults.add( result );
    }

    public void stop() {
        stopped = true;
    }

    private synchronized boolean allTasksComplete() {
        for (TaskDetails td : workstates.keySet()) {
            if (!workstates.get( td ).equals( State.COMPLETED )) {
                return false;
            }
        }
        return true;
    }

    private void shutdown() {
        log( "Tasks complete...writing results file and shutting down." );
        writeResults();
        try {
            UnicastRemoteObject.unexportObject( this, true );
            log( "Unexported." );
        } catch (NoSuchObjectException e) {
            logError( "Error unexporting in shutdown.", e );
        }
        try {
            registry.unbind( Distributor.SERVICE_NAME );
            log( "Unbound." );
        } catch (Exception e) {
            logError( "Error unbinding in shutdown.", e );
        }
        fileHandler.flush();
        fileHandler.close();
    }

    private void log( String message ) {
        logger.log( Level.INFO, message );
        fileHandler.flush();
    }

    private void logError( String message, Throwable throwable ) {
        logger.log( Level.WARNING, message, throwable );
    }

    private void logState() {
        log( ">>>>>>>>>>>>>>>>>>> Logging state..." );
        for (TaskDetails td : workstates.keySet()) {
            StringBuilder sb = new StringBuilder( td.packageName() );
            sb.append( " -> " );
            sb.append( workstates.get( td ) );
            log( sb.toString() );
        }
        log( "State logged <<<<<<<<<<<<<<<<<<<" );
    }

    private void writeResults() {
        File resultsFile = new File( System.getProperty( "user.dir" ), settings.collatedResultsFileName() );
        log( "Writing results to '" + resultsFile.getAbsolutePath() + "'" );
        try {
            BufferedWriter writer = new BufferedWriter( new FileWriter( resultsFile ) );
            for (TaskResult taskResult : unitTestResults) {
                writer.write( taskResult.resultsFileContents() );
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            logError( "Could not write results, as shown.", e );
        }
    }

    private class ShutdownThread extends Thread {
        private ShutdownThread() {
            super( "GTADistributor.ShutdownThread" );
            setDaemon( false );
        }

        public void run() {
            while (!stopped) {
                try {
                    Thread.sleep( PAUSE_TIME );
                } catch (InterruptedException e) {
                    //Ignore.
                }
                if (allTasksComplete()) {
                    shutdown();
                    return;
                }
            }
            //Escape from the loop without return only happens if stop has been called. Need to shutdown.
            shutdown();
        }
    }
}
