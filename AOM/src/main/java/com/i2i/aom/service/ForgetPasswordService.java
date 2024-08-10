package com.i2i.aom.service;

import com.i2i.aom.encryption.CustomerPasswordEncoder;
import com.i2i.aom.repository.CustomerRepository;
import com.i2i.aom.request.ForgetPasswordRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;

@Service
public class ForgetPasswordService {
    private final CustomerRepository customerRepository;
    private final CustomerPasswordEncoder customerPasswordEncoder;
    private final JavaMailSender javaMailSender;

    public ForgetPasswordService(CustomerRepository customerRepository,
                                 CustomerPasswordEncoder customerPasswordEncoder,
                                 JavaMailSender javaMailSender) {
        this.customerRepository = customerRepository;
        this.customerPasswordEncoder = customerPasswordEncoder;
        this.javaMailSender = javaMailSender;
    }


    public String forgetPassword(ForgetPasswordRequest request) throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException, MessagingException {
        if (!customerRepository.checkCustomerExists(request.email(), request.TCNumber())) {
            throw new IllegalArgumentException("Customer does not exist");
        }

        String newPassword = generateRandomPassword();
        String encryptedPassword = customerPasswordEncoder.encrypt(newPassword);

        customerRepository.updatePasswordInOracle(request.email(), request.TCNumber(), encryptedPassword);
        customerRepository.updatePasswordInVoltDB(request.email(), request.TCNumber(), encryptedPassword);

        sendEmail(request.email(), newPassword);
        return "Please check your mail address.";
    }

    private String generateRandomPassword() {
        final String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        final String numbers = "0123456789";

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder(15);

        password.append(upperCaseLetters.charAt(secureRandom.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(secureRandom.nextInt(lowerCaseLetters.length())));
        password.append(numbers.charAt(secureRandom.nextInt(numbers.length())));

        String allChars = upperCaseLetters + lowerCaseLetters + numbers;
        for (int i = 4; i < 15; i++) {
            password.append(allChars.charAt(secureRandom.nextInt(allChars.length())));
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int randomIndex = secureRandom.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[randomIndex];
            passwordArray[randomIndex] = temp;
        }

        return new String(passwordArray);
    }

    private void sendEmail(String email, String newPassword) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("Your New Password");

        String body = "<p>Hello,</p>"
                + "<p>Your password has been reset. Here is your new password:</p>"
                + "<h3>" + newPassword + "</h3>"
                + "<p>Please keep it safe and secure. You will need to use this password to log in to your account.</p>"
                + "<p>Best regards,<br/><b>Evrencell Team </b></p>";

        helper.setText(body, true);

        javaMailSender.send(message);
    }

}
