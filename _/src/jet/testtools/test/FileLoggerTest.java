package jet.testtools.test;


import jet.testtools.*;

import java.io.*;
import java.util.logging.*;

public class FileLoggerTest {

    private void productionMethodThatGeneratesAnEntry( String entry ) {
        FileLogger.log( new LogRecord( Level.INFO, entry ) );
    }

    public boolean rollLogOverTest() {
        FileLogger.rollLogOver();
        //Log should now be empty.
        final File logFile = new File( FileLogger.logDirName, "log.txt.0" );
        boolean isClear = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return Files.contents( logFile ).equals( "" );
            }
        }, 1000 );
        assert isClear;
        //Log a message and wait for it to appear.
        final String message = "You must never touch the claw!";
        FileLogger.log( new LogRecord( Level.WARNING, message ) );
        boolean logContainsMessage = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return Files.contents( logFile ).contains( message );
            }
        }, 1000 );
        assert logContainsMessage;
        //Roll, and wait for the log to be empty.
        FileLogger.rollLogOver();
        isClear = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return Files.contents( logFile ).equals( "" );
            }
        }, 1000 );
        assert isClear;
        //The rolled log should contain the message.
        File rolled = new File( FileLogger.logDirName, "log.txt.1" );
        assert Files.contents( rolled ).contains( message );
        return true;
    }

    public boolean logTest() {
        //Roll the log to clear it.
        FileLogger.rollLogOver();
        //Call the method that should generate a log entry.
        final String entry = "This is a normal transaction";
        productionMethodThatGeneratesAnEntry( entry );
        //Wait for the entry to appear in the log file.
        final File logFile =
                new File( FileLogger.logDirName, "log.txt.0" );
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return Files.contents( logFile ).contains( entry );
            }
        }, 1000 );

        return true;
    }

    private void productionMethodThatUsedToGenerateException( boolean flag ) {
        if (flag) {
            FileLogger.log( new LogRecord( Level.SEVERE, "Exception: Message indicating serious error" ) );
        }
    }

    public boolean exceptionNoLongGeneratedTest() {
        //Roll the log to clear it.
        FileLogger.rollLogOver();

        //Calling this method used to cause an exception
        //stack trace to be written in the log file.
        productionMethodThatUsedToGenerateException( false );

        //Log a marker entry.
        final String marker = "This is a marker entry";
        FileLogger.log( new LogRecord( Level.INFO, marker ) );
        //Wait for the marker to appear.
        final File logFile =
                new File( FileLogger.logDirName, "log.txt.0" );
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return Files.contents( logFile ).contains( marker );
            }
        }, 1000 );

        //Did the exception stack trace appear in the log?
        assert !Files.contents( logFile ).contains( "Exception" );

        return true;
    }
}