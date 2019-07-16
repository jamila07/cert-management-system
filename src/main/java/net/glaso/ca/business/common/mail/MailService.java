package net.glaso.ca.business.common.mail;

import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MailService {
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    public void sendMail( MailSender mailSender, String title, String body, String recirecipient ) {
        executorService.submit(() -> {
            try {
                mailSender.sendSimpleMail( title, body, recirecipient );
            } catch (MessagingException e) {
                throw new RuntimeException( e );
            }
        });
    }
}
