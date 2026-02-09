package com.example.notification_service.service.concretes;

import com.example.notification_service.service.abstracts.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender ;

    @Override
    public void sendMail(String to, String subject, String htmlContent) {
        System.out.println("1111111111111111111-METHOD İÇİ -MAIL SERVICE ÇAĞRILDI!");

        MimeMessage message = mailSender.createMimeMessage();
        try {
            System.out.println("222222222-TRY başı - MAIL SERVICE ÇAĞRILDI!");
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent,true);

            mailSender.send(message);
            System.out.println("222222222-TRY sonu - MAIL SERVICE ÇAĞRILDI!");



        }catch(Exception e) {
            throw new RuntimeException("Email could not send" + to);
        }


    }
}
