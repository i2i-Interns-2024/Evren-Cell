// ForgetPasswordService.java
package com.i2i.aom.service;

import com.i2i.aom.encryption.CustomerPasswordEncoder;
import com.i2i.aom.helper.OracleConnection;
import com.i2i.aom.helper.VoltDBConnection;
import com.i2i.aom.model.Notification;
import com.i2i.aom.repository.CustomerRepository;
import com.i2i.aom.request.ForgetPasswordRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;

@Service
public class ForgetPasswordService {
    private final CustomerRepository customerRepository;
    private final CustomerPasswordEncoder customerPasswordEncoder;
    private final JavaMailSender javaMailSender;
    private final VoltDBConnection voltDBConnection;
    private final OracleConnection oracleConnection;

    public ForgetPasswordService(CustomerRepository customerRepository,
                                 CustomerPasswordEncoder customerPasswordEncoder,
                                 JavaMailSender javaMailSender, VoltDBConnection voltDBConnection, OracleConnection oracleConnection) {
        this.customerRepository = customerRepository;
        this.customerPasswordEncoder = customerPasswordEncoder;
        this.javaMailSender = javaMailSender;
        this.voltDBConnection = voltDBConnection;
        this.oracleConnection = oracleConnection;
    }

    public String forgetPassword(ForgetPasswordRequest request) {
        try {
            if (!checkCustomerExists(request.email(), request.TCNumber())) {
                throw new IllegalArgumentException("Customer does not exist");
            }

            String newPassword = generateRandomPassword();
            String encryptedPassword = customerPasswordEncoder.encrypt(newPassword);

            updatePassword(request.email(), request.TCNumber(), encryptedPassword);

            int customerIdOracle = retrieveCustomerIdFromOracle(request.email(), request.TCNumber());
            int customerIdVolt = retrieveCustomerIdFromVoltDB(request.email(), request.TCNumber());

            insertNotificationLogs("Password Reset", new Timestamp(System.currentTimeMillis()), customerIdOracle, customerIdVolt);

            sendEmail(request.email(), newPassword);

            return "Please check your mail address.";
        } catch (SQLException | ClassNotFoundException | IOException | ProcCallException | InterruptedException | MessagingException e) {
            e.printStackTrace();
            return "An error occurred while processing your request.";
        }
    }

    private boolean checkCustomerExists(String email, String tcNumber) throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        return customerRepository.checkCustomerExists(email, tcNumber);
    }

    private void updatePassword(String email, String tcNumber, String encryptedPassword) throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        customerRepository.updatePasswordInOracle(email, tcNumber, encryptedPassword);
        customerRepository.updatePasswordInVoltDB(email, tcNumber, encryptedPassword);
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

    private int retrieveCustomerIdFromOracle(String email, String tcNumber) throws SQLException, ClassNotFoundException {
        Connection connection = oracleConnection.getOracleConnection();
        CallableStatement oracleStatement = connection.prepareCall("{call SELECT_CUSTOMER_ID_BY_EMAIL_AND_TCNUMBER(?, ?, ?)}");
        oracleStatement.setString(1, email);
        oracleStatement.setString(2, tcNumber);
        oracleStatement.registerOutParameter(3, Types.INTEGER);
        oracleStatement.execute();
        int customerId = oracleStatement.getInt(3);
        oracleStatement.close();
        connection.close();
        return customerId;
    }

    private int retrieveCustomerIdFromVoltDB(String email, String tcNumber) throws IOException, ProcCallException, InterruptedException {
        Client voltClient = voltDBConnection.getClient();
//        ClientResponse voltResponse = voltClient.callProcedure("GET_CUSTOMER_ID_BY_EMAIL_AND_TCNUMBER", email, tcNumber);
        ClientResponse voltResponse = voltClient.callProcedure("GET_CUSTOMER_ID_BY_MAIL_AND_TCNO", email, tcNumber);
        VoltTable voltTable = voltResponse.getResults()[0];
        int customerId = 0;
        if (voltTable.advanceRow()) {
            customerId = (int) voltTable.getLong("CUST_ID");
        }
        voltClient.close();
        return customerId;
    }

    private void insertNotificationLogs(String notificationType, Timestamp notificationTime, int customerIdOracle, int customerIdVolt) throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        customerRepository.insertNotificationLogInOracle(notificationType, notificationTime, customerIdOracle);
        customerRepository.insertNotificationLogInVoltDB(notificationType, notificationTime, customerIdVolt);
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
}