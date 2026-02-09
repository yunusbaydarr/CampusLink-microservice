package com.example.notification_service.service.abstracts;

public interface MailService {

        void sendMail(String to, String subject, String htmlContent);
    }


