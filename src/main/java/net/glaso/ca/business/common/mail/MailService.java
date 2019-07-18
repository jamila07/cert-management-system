package net.glaso.ca.business.common.mail;

import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MailService {
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    public void sendMail( MailSender mailSender, String title, String body, String recipient ) {
        executorService.submit(() -> {
            try {
                mailSender.sendSimpleMail( title, body, recipient );
            } catch (MessagingException e) {
                throw new RuntimeException( e );
            }
        });
    }
}
