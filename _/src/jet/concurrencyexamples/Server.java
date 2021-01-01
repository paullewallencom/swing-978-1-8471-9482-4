package jet.concurrencyexamples;

import jet.testtools.*;

import javax.mail.*;
import java.io.*;

/**
 * Used as an example in Chapter 12 and in Chapter 14.
 */
public class Server {
    public static final String HOUSEKEEPING = "Housekeeping";

    private File inputDir;
    private File outputDir;
    private File archiveDir;
    private String smtpHostAddress;
    private String emailFromAddress;

    public Server( File inputDir, File outputDir, File archiveDir, String smtpHostAddress, String emailFromAddress ) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.archiveDir = archiveDir;
        this.smtpHostAddress = smtpHostAddress;
        this.emailFromAddress = emailFromAddress;
    }

    public void housekeeping() {
        new Thread( HOUSEKEEPING ) {
            public void run() {
                File[] filesToZip = outputDir.listFiles();
                File zipFile = new File( archiveDir, "archived.zip" );
                Files.zip( filesToZip, zipFile );
                //delete the original files
                for (File file : filesToZip) {
                    file.delete();
                }
            }
        }.start();
    }

    public void process() {
        File[] inputFiles = inputDir.listFiles();
        for (File file : inputFiles) {
            try {
                Files.copyTo( file, outputDir );
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void archive() {
        final int THREADS = 5;
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread( "Archiver " + i ) {
                public void run() {
                    Waiting.pause( 5000 );
                }
            };
            threads[i].start();
        }
    }

    public void sendSupportEmail( String subject, String body ) throws MessagingException {
        SMTPSender sender = new SMTPSender( smtpHostAddress, emailFromAddress );
        sender.send( "jet@grandtestauto.org", subject, body );
    }
}
