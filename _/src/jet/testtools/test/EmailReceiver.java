package jet.testtools.test;

import jet.testtools.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import java.io.*;

/**
 * Manages a connection to the INBOX of an email account.
 */
public class EmailReceiver {
    private Store mailStore;
    public static final int MAIL_CHECK_PERIOD_IN_MILLIS = 10000;
    private static final String INBOX = "INBOX";
    private Settings settings;

    public interface Settings {
        String getSMTPUname();

        String getSMTPPwd();

        String getPOPHost();

        String getPOPUname();

        String getPOPPwd();

        String getProtocol();

        File directoryForAttachments();
    }

    public EmailReceiver( Settings settings ) {
        this.settings = settings;
        try {
            final PasswordAuthentication pa = new PasswordAuthentication( settings.getSMTPUname(), settings.getSMTPPwd() );
            Authenticator authenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
            Session session = Session.getDefaultInstance( System.getProperties(), authenticator );
            mailStore = session.getStore( settings.getProtocol() );
            mailStore.connect( settings.getPOPHost(), settings.getPOPUname(), settings.getPOPPwd() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Note that other messages may be received after this remove is done, but the before
     * the test is performed.
     */
    public void removeAllMessages() {
        try {
            Folder folder = mailStore.getFolder( INBOX );
            folder.open( Folder.READ_WRITE );
            Message[] msgs = folder.getMessages();
            for (int nMsg = 0; nMsg < msgs.length; nMsg++) {
                Message msg = msgs[nMsg];
                msg.setFlag( Flags.Flag.DELETED, true );
            }
            //Close the folder to delete these messages.
            folder.close( true );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        //give the mail server some time to delete the messages, but don't assert if
        //there are still some remaining, as new ones may have been received in the meantime.
        waitForNoMessages();
    }

    private void waitForNoMessages() {
        Waiting.ItHappened ih = new Waiting.ItHappened() {
            public boolean itHappened() {
                boolean isEmpty = false;
                try {
                    Folder folder = mailStore.getFolder( INBOX );
                    folder.open( Folder.READ_ONLY );
                    int numberOfMessages = folder.getMessageCount();
                    if (numberOfMessages > 0) {
                        Waiting.pause( 2000 );
                    }
                    isEmpty = numberOfMessages == 0;
                    folder.close( true );
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return isEmpty;
            }

            ;

        };
        boolean foundNoMessages = Waiting.waitFor( ih, 120 * 1000 );
        if (!foundNoMessages) System.out.println( "Warning! Inbox folder was not empty, or could not be opened" );
    }

    /**
     * Waits for the specified message, and saves any attachments.
     */
    public void checkForMessage( final String address, final String subject, final String text, int secsToWait ) {
        if (secsToWait <= 30) System.out.println( "Must wait more than 30 secs" );
        assert secsToWait > 30;
        Waiting.ItHappened ih = new Waiting.ItHappened() {
            public boolean itHappened() {
                boolean found = false;
                try {
                    Folder inbox = mailStore.getFolder( INBOX );
                    inbox.open( Folder.READ_ONLY );
                    Message[] allMessages = inbox.search( new SearchTerm() {
                        public boolean match( Message message ) {
                            return true;
                        }
                    } );

                    System.out.println( "found messages: " + allMessages.length );
                    for (int i = 0; i < allMessages.length; i++) {
                        Message msg = allMessages[i];
                        System.out.println( "message to: " + msg.getAllRecipients()[0] );
                        msg.setFlag( Flags.Flag.DELETED, true );
                        if (msg.getSubject().equals( subject )) {
                            System.out.println( "subject match!" );
                            if (!msg.getAllRecipients()[0].toString().equals( address )) {
                                continue;
                            }
                            //Compare the email body after stripping whitespace.
                            MimeMultipart mm = (MimeMultipart) msg.getContent();
                            StringBuffer buf = new StringBuffer();
                            int ch = -1;
                            while ((ch = msg.getInputStream().read()) != -1) {
                                buf.append( (char) ch );

                            }
                            System.out.println( "buf: " + buf );
//              String canonicalContent = TestHelper.trimAndCompactWhitespaces(msg.getContent().toString());
//              String canonicalExpected = TestHelper.trimAndCompactWhitespaces(text);
//              if (!canonicalExpected.equals(canonicalContent)) {
//                check next received message
//                continue;
//              }

                            found = true;
                            //save any attachments
                            Object content = msg.getContent();
                            if (content instanceof Multipart) {
                                Multipart mp = (Multipart) content;
                                for (int j = 0, nMPCount = mp.getCount(); j < nMPCount; j++) {
                                    Part part = mp.getBodyPart( j );
                                    String disposition = part.getDisposition();
                                    if ((disposition != null) && (disposition.equalsIgnoreCase( Part.ATTACHMENT ) || disposition.equalsIgnoreCase( Part.INLINE ))) {
                                        saveFile( part.getFileName(), part.getInputStream() );
                                    }
                                }
                            }
                            break;//So we'll clean up and return true.
                        }
                    }
                    inbox.close( true );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return found;
            }
        };
        //poll every 5 secs
        assert Waiting.waitFor( ih, secsToWait * 1000, 5000 );
    }

    private void saveFile( String sFilename, InputStream is ) throws IOException {
        File fileFolder = settings.directoryForAttachments();
        File savedFile = new File( fileFolder, sFilename );
        //cleanup from a previous test
        if (savedFile.exists()) {
            savedFile.delete();
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream( is );
            OutputStream os = new FileOutputStream( savedFile );
            bos = new BufferedOutputStream( os );
            int data;
            while ((data = is.read()) != -1) {
                os.write( data );
            }
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }


    public void close() {
        try {
            mailStore.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}