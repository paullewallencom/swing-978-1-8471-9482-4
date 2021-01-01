package jet.testtools.test;

import jet.testtools.*;

import java.net.*;

public class SMTPSenderTest {
    private SMTPSender sender;
    private SMTPProcessor mailServerProxy;

    public boolean sendTest() throws Exception {
        init( true );

        sender.send( "hermione@hogwarts.edu", "ron", "Ron is at Hogsmead" );
        waitForNumberOfMessages( 1 );
        SMTPMessage message = mailServerProxy.messagesReceived().iterator().next();
        Assert.equal( message.getToAddresses().size(), 1 );
        Assert.equal( message.getToAddresses().get( 0 ).toString(), "hermione@hogwarts.edu" );
        cleanup();
        return true;
    }

    private void waitForNumberOfMessages( final int n ) {
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return mailServerProxy.messagesReceived().size() == n;
            }
        }, 10000 );
    }

    private void init( boolean start ) throws Exception {
        mailServerProxy = new SMTPProcessor( false );
        if (start) {
            mailServerProxy.start();
        }
        String emailHost = InetAddress.getLocalHost().getHostAddress();
        sender = new SMTPSender( emailHost, "harry@hogwarts.edu" );
    }

    private void cleanup() throws InterruptedException {
        mailServerProxy.shutdown();
    }

    //Stub tests for other methods as this is only a demonstration class.
    public boolean constructorTest() {
        return true;
    }
}
