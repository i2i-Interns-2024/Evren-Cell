package com.i2i.evrencell.nf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.i2i.evrencell.kafka.message.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationOperations implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(NotificationOperations.class);
    private static final Properties mailProperties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("mailConfig.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find mailConfig.properties");
            }
            mailProperties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading properties file", ex);
        }
    }

    public static String getProperty(String key) {
        String envValue = System.getenv(key.toUpperCase().replace(".", "_"));
        if (envValue != null) {
            return envValue;
        }
        return mailProperties.getProperty(key);
    }

    public Session createMailSession() {

        Properties props = new Properties();
        props.put("mail.smtp.host", mailProperties.getProperty("mail.smtp.host"));
        props.put("mail.smtp.port", mailProperties.getProperty("mail.smtp.port"));
        props.put("mail.smtp.auth", mailProperties.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", mailProperties.getProperty("mail.smtp.starttls.enable"));

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = mailProperties.getProperty("mail.smtp.user");
                String password = mailProperties.getProperty("mail.smtp.password");
                return new PasswordAuthentication(username, password);
            }
        };

        return Session.getInstance(props, auth);
    }

    public void sendMail(Session session, NotificationMessage message) {
        MimeMessage msg = new MimeMessage(session);

        String fromEmail = mailProperties.getProperty("mail.smtp.user");
        try {
            msg.setFrom(new InternetAddress(fromEmail));
        } catch (MessagingException me) {
            logger.info("Invalid email address: " + fromEmail);
            me.printStackTrace();
        }

        String subjectType = "";
        String txtType = "";
        switch (message.getType()) {
            case VOICE -> {
                subjectType = "Konusma";
                txtType = "DK konusma";
            }
            case SMS -> {
                subjectType = "SMS";
                txtType = "SMS";
            }
            case DATA -> {
                subjectType = "Internet";
                txtType = "MB internet";
            }
        }

        try {
            msg.setSubject(subjectType + " hakki");
        } catch (MessagingException e) {
           logger.info("Error setting subject: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            msg.setText(String.format("Sayin %s %s, paketinize tanimli toplam %s %s hakkinizin %s%% kadarini %s tarihi itibari ile kullandiniz.",
                                message.getName(),message.getLastname(),message.getAmount(),txtType,message.getThreshold(),message.getTimestamp()));
        } catch (MessagingException e) {
            logger.info("Error setting text: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.getEmail(), false));
        } catch (MessagingException messagingException) {
            logger.info("Error setting recipient: " + messagingException.getMessage());
            messagingException.printStackTrace();
        }

        try {
            Transport.send(msg);
            logger.info("Email sent successfully to: " + message.getEmail());
        } catch (MessagingException e) {
            logger.info("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void close(){}
}
