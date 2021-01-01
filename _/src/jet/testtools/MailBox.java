package jet.testtools;

import javax.mail.*;
import java.util.*;

/**
 * Simple wrapper for a POP3 inbox, as seen
 * in Chapter 14.
 */
public class MailBox {

    private String host;
    private String user;
    private String password;

    public MailBox( String host, String user, String password ) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public void removeAllMessages() throws Exception {
        iterateMessages( new WorkForMessage() {
            public void doIt( Message message ) {
                try {
                    message.setFlag( Flags.Flag.DELETED, true );
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        } );
    }

    public Set<Message> getMessagesMatching(
            final String subject ) throws Exception {
        final Set<Message> result = new HashSet<Message>();
        iterateMessages( new WorkForMessage() {
            public void doIt( Message message ) {
                try {
                    if (subject.equals( message.getSubject() )) {
                        result.add( message );
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        } );
        return result;
    }

    private interface WorkForMessage {
        void doIt( Message message );
    }

    private void iterateMessages(
            WorkForMessage wfm ) throws Exception {
        Properties properties = new Properties();
        Session session = Session.getInstance( properties, null );
        Store store = session.getStore( "pop3" );
        store.connect( host, user, password );
        Folder inbox = store.getFolder( "INBOX" );
        inbox.open( Folder.READ_WRITE );
        Message[] allMessages = inbox.getMessages();
        for (Message message : allMessages) {
            wfm.doIt( message );
        }
        inbox.close( true );
        store.close();
    }
}
