package jet.testtools;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

/**
 * Simple demonstration class for sending email
 * messages, as discussed in Chapter 14.
 */
public class SMTPSender {
    private String host;
    private InternetAddress from;

    public SMTPSender( String host, String fromAddress ) throws AddressException {
        this.host = host;
        from =new InternetAddress( fromAddress );
    }

    public void send( String address, String subject, String body ) throws MessagingException {
        Properties properties = System.getProperties();
        properties.put( "mail.smtp.host", host );
        Session session = Session.getDefaultInstance( System.getProperties(), null );
        MimeMessage message = new MimeMessage( session );
        message.setFrom( from );
        message.setRecipient( Message.RecipientType.TO, new InternetAddress( address ) );
        message.setSentDate( new Date() );
        message.setSubject( subject );
        MimeBodyPart mimeBody = new MimeBodyPart();
        mimeBody.setText( body );
        Multipart mp = new MimeMultipart();
        mp.addBodyPart( mimeBody );
        message.setContent( mp );
        Transport.send( message );
    }
}