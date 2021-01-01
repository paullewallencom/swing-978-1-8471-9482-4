package jet.testtools.test;

import jet.testtools.*;

public class MailBoxTest {
    private MailBox mailBox;
    private SMTPSender sender;

    //This is just an example class, and we don't always want to
    //run the tests (particularly when on the train or when
    //we have removed the details of real email accounts
    //from the code for shipping).
    private boolean online = false;

    public boolean removeAllMessagesTest() throws Exception {
        if (!online) return true;
        init();
        //Send a message.
        String subject = "Beethoven";
        sender.send( "jet@grandtestauto.org", subject, "Wrote a lot of wonderful music" );
        //Wait for it.
        assert waitForMessage( subject ) : "Did not get email message!";
        //Remove all messages.
        mailBox.removeAllMessages();
        //Check that it is no longer there.
        assert !waitForMessage( subject ) : "Message not removed!";

        return true;
    }

    public boolean getMessagesMatchingTest() throws Exception {
        if (!online) return true;
        init();
        //Remove any messages left over from previous tests.
        mailBox.removeAllMessages();
        //No messages matching either subject.
        String subject1 = "Beethoven";
        String subject2 = "Brahms";
        assert mailBox.getMessagesMatching( subject1 ).isEmpty();
        assert mailBox.getMessagesMatching( subject2 ).isEmpty();
        //Send a message.
        sender.send( "jet@grandtestauto.org", subject1, "Wrote a lot of wonderful music" );
        //Wait for it.
        assert waitForMessage( subject1 ) : "Did not get email message!";
        //Still none matching second subject.
        assert !mailBox.getMessagesMatching( subject1 ).isEmpty();
        assert mailBox.getMessagesMatching( subject2 ).isEmpty();

        return true;
    }

    public boolean constructorTest() {
        if (!online) return true;

        return true;
    }

    private boolean waitForMessage( final String subject ) {
        return Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                try {
                    return mailBox.getMessagesMatching( subject ).size() > 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }, 60000, 5000 );
    }

    private void init() throws Exception {
        //Values for a real email account need to be put here.
        mailBox = new MailBox( "host", "user", "password" );
        //Values for a real email account need to be put here.
        sender = new SMTPSender( "host", "from.address" );
    }
}