package net.glaso.ca.framework.utils;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {

    private String host;
    private String smtpHost;
    private int port;
    private String userName;
    private String password;

    private static Session mailSession;

    public MailSender(String host, String smtpHost, int port, String userName, String password ) {
        this.host = host;
        this.smtpHost = smtpHost;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    public void sendSimpleMail( String subject, String body, String recipient ) throws MessagingException, javax.mail.MessagingException {

        setSession();

        Message mimeMessage = new MimeMessage( mailSession );

        String from = new StringBuilder( userName )
                .append( "@" )
                .append( host ).toString();

        String to = new StringBuilder( recipient )
                .append( "@" )
                .append( host ).toString();

        mimeMessage.setFrom( new InternetAddress( from ) );
        mimeMessage.setRecipient( Message.RecipientType.TO, new InternetAddress( to ) );

        mimeMessage.setSubject( subject );
        mimeMessage.setText( body );

        Transport.send( mimeMessage );
    }

    private void setSession() {
        Properties props = System.getProperties();

        // SMTP INFO
        props.put( "mail.smtp.host", smtpHost );
        props.put( "mail.smtp.port", port );
        props.put( "mail.smtp.auth", "true" );
        props.put( "mail.smtap.ssl.enable", "true" );
        props.put( "mail.smtp.ssl.trust", smtpHost );

        // create session
        mailSession = Session.getDefaultInstance(props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( userName, password );
            }
        });

        mailSession.setDebug( true );
    }
}
