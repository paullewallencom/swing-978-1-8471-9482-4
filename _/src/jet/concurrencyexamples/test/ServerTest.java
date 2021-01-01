package jet.concurrencyexamples.test;

import jet.concurrencyexamples.*;
import jet.testtools.*;
import jet.testtools.test.*;
import jet.testtools.test.testtools.*;

import javax.mail.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Used as an example in Chapters 12 and 14.
 */
public class ServerTest {

    private File inputDir;
    private File outputDir;
    private File archiveDir;
    private Server server;
    private SMTPProcessor mailServer;
    private MailBox mailBox;

    private void init( boolean useLocalMailServer ) throws Exception {
        //Clean up any files from a previous test.
        File tempDir = Files.cleanedTempDir();
        outputDir = new File( tempDir, "outputDir" );
        outputDir.mkdirs();
        inputDir = new File( tempDir, "inputDir" );
        inputDir.mkdirs();
        archiveDir = new File( tempDir, "archiveDir" );
        archiveDir.mkdirs();
        if (useLocalMailServer) {
        String emailHost = InetAddress.getLocalHost().getHostAddress();
        server = new Server( inputDir, outputDir, archiveDir,
                emailHost, "harry@hogwarts.edu" );
        mailServer = new SMTPProcessor( false );//No debug.
        mailServer.start();
        } else {
            //Values for a real email account need to be put here.
            mailBox = new MailBox( "host", "user", "password" );
            //Values for a real email account need to be put here.            
            server = new Server( inputDir,  outputDir, archiveDir, "host.address", "email.address" );
        }
    }

    private void cleanup() {
        if (mailServer != null) {
            mailServer.shutdown();
        }
    }

    public boolean housekeepingTest() throws Exception {
        init( true );

        //Put 2 configured files into the output directory.
        File file1 = new File( Testtools.datafile1_txt );
        File file2 = new File( Testtools.datafile2_txt );
        Files.copyTo( file1, outputDir );
        Files.copyTo( file2, outputDir );

        //Sanity check that these are the only 2 files in it.
        Assert.equal( outputDir.listFiles().length, 2 );

        //run the housekeeping task
        server.housekeeping();

        //wait for it to complete
        boolean completed = TestHelper.waitForNamedThreadToFinish(
                Server.HOUSEKEEPING, 2000 );
        assert completed : "Housekeeping thread did not stop!";
        //check that the archive file has been created
        File expectedZip = new File( archiveDir, "archived.zip" );
        assert expectedZip.exists();

        //Check that the 2 files
        //put into the output dir have been deleted.
        Assert.equal( outputDir.listFiles().length, 0 );

        //Check the contents of the zip files.
        Files.unzip( expectedZip.getPath(), Files.tempDir() );
        File retrieved1 = new File( Files.tempDir(), file1.getName() );
        File retrieved2 = new File( Files.tempDir(), file2.getName() );
        Assert.equal( Files.contents( file1 ),
                Files.contents( retrieved1 ) );
        Assert.equal( Files.contents( file2 ),
                Files.contents( retrieved2 ) );

        cleanup();
        return true;
    }

    public boolean archiveTest() throws Exception {
        init( true );
        //Get an initial count of the active threads.
        final int numThreadAtStart = Thread.activeCount();

        //Start the archiving process. This takes
        //at least 5 seconds.
        server.archive();

        //Check whether we now have 5 additional threads.
        //There is really no race condition here as server.archive()
        //called above takes at least 5 seconds.
        Assert.equal( Thread.activeCount(), numThreadAtStart + 5 );

        //Wait for 10 secs for all archive threads to complete
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return Thread.activeCount() == numThreadAtStart;
            }
        }, 10000 );

        //Check whether the archiving was performed....

        cleanup();
        return true;
    }

    public boolean processTest() throws Exception {
        init( true );
        //Unzip the configured input files into the input dir.
        Files.cleanDirectory( inputDir );
        Files.unzip( Testtools.input_zip, inputDir );

        //Have them processed.
        server.process();

        //Zip the processed files in the output directory
        File processed = new File( outputDir, "processed.zip" );
        Files.zip( outputDir.listFiles(), processed );

        //Compare processed files with configured output files
        assert Files.equalZips( processed, new File( Testtools.output_zip ) );

        cleanup();
        return true;
    }

    /**
     * Test of the mail functionality using an email server
     * running inside the test JVM.
     */
    public boolean sendSupportEmailTest() throws Exception {
        init( true );

        //send a support email.
        String subject = "Support message from site x";
        String body = "Test message only";
        server.sendSupportEmail( subject, body );

        //Wait for the expected message to arrive.
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return mailServer.messagesReceived().size() == 1;
            }
        }, 1000 );

        //Now check the message details.
        Collection<SMTPMessage> messages = mailServer.messagesReceived();
        assert messages.size() == 1;
        SMTPMessage message = messages.iterator().next();
        Assert.equal( message.getFromAddress().toString(),
                "harry@hogwarts.edu" );
        //etc.

        cleanup();
        return true;
    }

    /**
     * Test of the mail functionality using a
     * remote mail server.
     */
    public boolean remoteEmailTest() throws Exception {
        if (2 > 1) return true;//Comment out this line when connected to the internet.
        init( false );//Use remote mail account and create mailBox.
        mailBox.removeAllMessages();

        //Send a support email.
        final String subject = "Support message from site x";
        String body = "Test message only";
        server.sendSupportEmail( subject, body );

        //Wait for up to a minute for the expected message to arrive.
        boolean gotIt = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                try {
                    return mailBox.getMessagesMatching(
                            subject ).size() > 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }, 60000, 5000 );
        assert gotIt : "Did not get expected message!";

        //Now check the message details.
        Set<Message> messages = mailBox.getMessagesMatching( subject );
        assert messages.size() == 1;
        //etc

        cleanup();
        return true;
    }

    //Dummy tests for other methods, as this is only a demo class.
    public boolean constructorTest() {
        return true;
    }
}
