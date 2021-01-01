package org.grandtestauto.util;

import org.grandtestauto.test.*;

import java.io.*;

/**
 * Reads the standard output and standard error streams
 * from a process. Each stream is read in a separate thread.
 *
 * @author Tim Lavers
 */
public class ProcessReader {
    public static String[] readProcess( Process p ) {
        final StringBuilder soutBuilder = new StringBuilder();
        final StringBuilder serrBuilder = new StringBuilder();
        try {
            BufferedReader sout = new BufferedReader(
                    new InputStreamReader( p.getInputStream() ) );
            ProcessReader soutReader =
                    new ProcessReader( sout, soutBuilder );
            BufferedReader serr = new BufferedReader(
                    new InputStreamReader( p.getErrorStream() ) );
            ProcessReader serrReader =
                    new ProcessReader( serr, serrBuilder );
            soutReader.thread.start();
            serrReader.thread.start();
            soutReader.thread.join();
            serrReader.thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{soutBuilder.toString(), serrBuilder.toString()};
    }

    private final BufferedReader toRead;
    private final StringBuilder sb;
    private final Thread thread;

    private ProcessReader( BufferedReader sout, StringBuilder sb ) {
        this.toRead = sout;
        this.sb = sb;
        thread = new Thread( "ProcessReader" ) {
            public void run() {
                try {
                    String line = toRead.readLine();
                    while (line != null) {
                        ProcessReader.this.sb.append( line );
                        ProcessReader.this.sb.append( Helpers.NL );
                        line = toRead.readLine();
                    }
                } catch (Exception e) {
                    try {
                        toRead.close();
                    } catch (Exception e1) {
                        //Ignore.
                    }
                }
            }
        };
    }
}