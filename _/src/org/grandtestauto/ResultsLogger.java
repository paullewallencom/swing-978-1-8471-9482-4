package org.grandtestauto;

import java.io.*;
import java.util.logging.*;

/**
 * Handles all results logging.
 *
 * @author Tim Lavers
 */
public class ResultsLogger {

    /**
     * To which results are written.
     */
    private Logger logger;

    /**
     * Creates a ResultsLogger that logs results to the named file, if not null, and
     * optionally to the console.
     */
    public ResultsLogger( String resultsFileName, boolean logToConsole ) throws IOException {
        logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers( false );
        logger.setLevel( Level.ALL );
        //Add handlers as required.
        if (logToConsole) {
            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter( new ResultsFormatter() );
            logger.addHandler( consoleHandler );
        }
        if (resultsFileName != null) {
            OutputStream os = new FileOutputStream( resultsFileName );
            Handler fileHandler = new StreamHandler( os, new ResultsFormatter() );
            fileHandler.setFormatter( new ResultsFormatter() );
            logger.addHandler( fileHandler );
        }
    }

    public void log( String message, Throwable t ) {
        logger.log( Level.SEVERE, message, t );
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.flush();
        }
    }

    /**
     * Flush and close the handlers..
     */
    public void closeLogger() {
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
        }
    }
}
