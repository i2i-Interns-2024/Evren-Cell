package com.i2i.evrencell.aom.service;

import com.i2i.evrencell.aom.encryption.CustomerPasswordEncoder;
import com.i2i.evrencell.aom.helper.OracleConnection;
import com.i2i.evrencell.aom.repository.CustomerRepository;
import com.i2i.evrencell.aom.request.ForgetPasswordRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

@Service
public class ForgetPasswordService {
    private static final Logger logger = LogManager.getLogger(ForgetPasswordService.class);
    private final CustomerRepository customerRepository;
    private final CustomerPasswordEncoder customerPasswordEncoder;
    private final JavaMailSender javaMailSender;
    private final OracleConnection oracleConnection;

    public ForgetPasswordService(CustomerRepository customerRepository,
                                 CustomerPasswordEncoder customerPasswordEncoder,
                                 JavaMailSender javaMailSender,
                                 OracleConnection oracleConnection) {
        this.customerRepository = customerRepository;
        this.customerPasswordEncoder = customerPasswordEncoder;
        this.javaMailSender = javaMailSender;
        this.oracleConnection = oracleConnection;
    }

    public ResponseEntity<String> forgetPassword(ForgetPasswordRequest request) {
        try {
            logger.debug("Starting to reset password for customer with email: " + request.email() + " and TC number: " + request.TCNumber());
            if (!checkCustomerExists(request.email(), request.TCNumber())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer does not exist");
            }

            String newPassword = generateRandomPassword();
            String encryptedPassword = customerPasswordEncoder.encrypt(newPassword);

            try {
                sendEmail(request.email(), newPassword);
            } catch (SendFailedException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send email");
            } catch (MessagingException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred while sending email");
            }

            try {
                updatePassword(request.email(), request.TCNumber(), encryptedPassword);
            } catch (SQLException | ClassNotFoundException | IOException | ProcCallException | InterruptedException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred while resetting password");
            }

            int customerIdOracle = retrieveCustomerIdFromOracle(request.email(), request.TCNumber());
            try {
                insertNotificationLogs(new Timestamp(System.currentTimeMillis()), customerIdOracle);
            } catch (SQLException | ClassNotFoundException | IOException | ProcCallException | InterruptedException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred while inserting notification logs");
            }
            logger.debug("Password reset successfully for customer with email: " + request.email() + " and TC number: " + request.TCNumber());
            return ResponseEntity.ok("Please check your mail address.");
        } catch (SQLException | ClassNotFoundException | IOException | ProcCallException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred while resetting password");
        }
    }

    /**
     * This method checks if the customer exists in the database
     * @param email
     * @param tcNumber
     * @return boolean
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    private boolean checkCustomerExists(String email, String tcNumber) throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        return customerRepository.checkCustomerExists(email, tcNumber);
    }

    /**
     * This method updates the password of the customer in both Oracle and VoltDB
     * @param email
     * @param tcNumber
     * @param encryptedPassword
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    private void updatePassword(String email, String tcNumber, String encryptedPassword) throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        customerRepository.updatePasswordInOracle(email, tcNumber, encryptedPassword);
        customerRepository.updatePasswordInVoltDB(email, tcNumber, encryptedPassword);
    }

    /**
     * This method sends an email to the customer with the new password
     * The email contains the new password and a message to keep it safe and secure
     * @param email
     * @param newPassword
     * @throws MessagingException
     */
    private void sendEmail(String email, String newPassword) throws MessagingException {
        logger.debug("Sending email to customer with new password");
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
        logger.debug("Email sent successfully");
    }

    /**
     * This method retrieves the customer ID from Oracle to be used in the notification log
     *
     * @param email
     * @param tcNumber
     * @return int
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private int retrieveCustomerIdFromOracle(String email, String tcNumber) throws SQLException, ClassNotFoundException {
        logger.debug("Retrieving customer ID from Oracle");
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

    /**
     * This method inserts a notification log in both Oracle and VoltDB
     *
     * @param notificationTime
     * @param customerIdOracle
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    private void insertNotificationLogs(Timestamp notificationTime, int customerIdOracle)
            throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        customerRepository.insertNotificationLogInOracle("Password Reset", notificationTime, customerIdOracle);
    }


    /**
     * This method generates a random password for the customer
     * The password is 15 characters long and contains at least one uppercase letter, one lowercase letter, and one number
     * The rest of the characters are randomly selected from the pool of uppercase letters, lowercase letters, and numbers
     * The password is then shuffled to ensure randomness
     * @return String
     */
    private String generateRandomPassword() {
        logger.debug("Generating random password");
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
        logger.debug("Random password generated successfully");
        return new String(passwordArray);
    }
}