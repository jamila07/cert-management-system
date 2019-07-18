package net.glaso.ca.business.common.mail;

import net.glaso.ca.framework.init.MailSettings;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailSender {

    private String host;
    private String smtpHost;
    private int port;
    private String userName;
    private String password;

    private static Session mailSession;

    public MailSender() {
        this.host = MailSettings.getInstance().get( "host" );
        this.smtpHost = MailSettings.getInstance().get( "smtpHost" );
        this.port = Integer.parseInt( MailSettings.getInstance().get( "port" ) );
        this.userName = MailSettings.getInstance().get( "userName" );
        this.password = MailSettings.getInstance().get( "password" );
    }

    public MailSender(String host, String smtpHost, int port, String userName, String password ) {
        this.host = host;
        this.smtpHost = smtpHost;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    public void sendSimpleMail( String subject, String text, String recipient ) throws MessagingException {

        setSession();

        Message mimeMessage = new MimeMessage( mailSession );

        String from = new StringBuilder( userName )
                .append( "@" )
                .append( host ).toString();

        mimeMessage.setFrom( new InternetAddress( from ) );
        mimeMessage.setRecipient( Message.RecipientType.TO, new InternetAddress( recipient ) );

        mimeMessage.setSubject( subject );
        mimeMessage.setContent( text, "text/html; charset=utf-8");

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
