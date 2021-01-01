package org.grandtestauto.util.test;

import jet.testtools.*;
import org.apache.commons.io.*;

import java.io.*;

/**
 * This is a class to run in a separate proces for the
 * unit test of ProcessReader. Its main method prints
 * the contents of two files out to serr and sout.
 *
 * @author Tim Lavers
 */
public class ClassToRunInProcess {

    /**
     * Writes the contents of two files to sout and serr,
     * pausing between lines.
     * @param args args[0] is the name of the file to be written to sout, args[1] to serr
     */
    public static void main( String[] args ) {
        File f1 = new File( args[0] );
        File f2 = new File( args[1] );
        ReadWrite rw1 = new ReadWrite( f1, System.out );
        ReadWrite rw2 = new ReadWrite( f2, System.err );
        new Thread( rw1, "ClassToRunInProcess.rw1" ).start();
        new Thread( rw2, "ClassToRunInProcess.rw2" ).start();
    }
}

class ReadWrite implements Runnable {
    File f;
    PrintStream ps;

    ReadWrite( File f, PrintStream ps ) {
        this.f = f;
        this.ps = ps;
    }

    public void run() {
        try {
            LineIterator itor = FileUtils.lineIterator( f );
            while (itor.hasNext()) {
                String line = itor.nextLine();
                ps.println( line );
                Waiting.pause( 100 );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

