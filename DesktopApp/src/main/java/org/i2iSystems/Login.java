package org.i2iSystems;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;


import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Login extends JFrame {


    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPasswordField passwordField;
    private JTextField textFieldPhone;
    private String logoPath = ConfigLoader.getProperty("logoPath");
    private String rememberMePath = ConfigLoader.getProperty("rememberMePath");
    private String InfoPath = ConfigLoader.getProperty("InfoPath");
    private String urlPathLogin = ConfigLoader.getProperty("urlPathLogin");
    private String backGroundColor = ConfigLoader.getProperty("backGroundColor");
    private String foreGroundColor = ConfigLoader.getProperty("foreGroundColor");
    private String textType = ConfigLoader.getProperty("textType");

    private JLabel label1;
    private JLabel label2;
    private JLabel label3;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Login frame = new Login();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Login() {


        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 848, 657);
        contentPane = new JPanel();
        contentPane.setBackground(SystemColor.text);
        contentPane.setForeground(SystemColor.menu);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setForeground(SystemColor.textHighlightText);
        lblNewLabel.setBounds(284, 54, 247, 247);
        contentPane.add(lblNewLabel);

        ImageIcon originalIcon = new ImageIcon(getClass().getResource(logoPath));

        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(lblNewLabel.getWidth(), lblNewLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        lblNewLabel.setIcon(scaledIcon);

        // Phone Field
        textFieldPhone = new JTextField();
        textFieldPhone.setBorder(null);
        textFieldPhone.setFont(new Font(textType, Font.PLAIN, 22));
        textFieldPhone.setBounds(284, 311, 255, 33);
        contentPane.add(textFieldPhone);
        textFieldPhone.setColumns(10);
        textFieldPhone.setBackground(Color.decode(backGroundColor));
        textFieldPhone.setForeground(Color.decode(foreGroundColor));

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBorder(null);
        passwordField.setFont(new Font(textType, Font.PLAIN, 22));
        passwordField.setBounds(284, 355, 255, 33);
        passwordField.setEchoChar('*');
        passwordField.setBackground(Color.decode(backGroundColor));
        passwordField.setForeground(Color.decode(foreGroundColor));
        contentPane.add(passwordField);

        // ChackBox Field
        JCheckBox chckbxNewCheckBox = new JCheckBox("Remember me");
        chckbxNewCheckBox.setFocusable(false);
        chckbxNewCheckBox.setFont(new Font(textType, Font.PLAIN, 14));
        chckbxNewCheckBox.setBackground(Color.WHITE);
        chckbxNewCheckBox.setBounds(294, 395, 127, 23);
        contentPane.add(chckbxNewCheckBox);

        JRadioButton rdbtnNewRadioButton = new JRadioButton("");
        rdbtnNewRadioButton.setBounds(550, 361, 23, 23);
        rdbtnNewRadioButton.setBackground(Color.white);
        rdbtnNewRadioButton.setForeground(Color.decode(foreGroundColor));
        rdbtnNewRadioButton.setBorder(null);
        contentPane.add(rdbtnNewRadioButton);

        // Add an action listener to the radio button
        rdbtnNewRadioButton.addActionListener(new ActionListener() {
            private boolean passwordVisible = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (passwordVisible) {
                    passwordField.setEchoChar('*'); // Hide the password
                } else {
                    passwordField.setEchoChar((char) 0); // Show the password
                }
                passwordVisible = !passwordVisible; // Toggle the visibility state
            }
        });

        updateCheckBoxFromFile(rememberMePath, chckbxNewCheckBox); //CHECKBOX SETTINGS

        if (chckbxNewCheckBox.isSelected()) {                      //Get userInfo
            loadUserInfo(InfoPath, textFieldPhone, passwordField);
        }

        JButton btnLogin = new JButton("Login");
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(Color.decode(foreGroundColor));
        btnLogin.setBorder(null);

        btnLogin.addActionListener(e -> {
            String phone = textFieldPhone.getText();
            String password = new String(passwordField.getPassword());
            if (authenticateUser(phone, password)) {
                if (chckbxNewCheckBox.isSelected()) {
                    WriteTXT txt = new WriteTXT();
                    txt.bool(null, "1");
                    txt.userInfo(phone, password);
                } else {
                    WriteTXT txt = new WriteTXT();
                    txt.bool(null, "0");
                }
            } else {
                //System.out.println("ConnectionFaild...");
            }
        });

        btnLogin.setFont(new Font(textType, Font.PLAIN, 20));
        btnLogin.setBounds(328, 425, 154, 48);
        contentPane.add(btnLogin);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBorder(null);
        btnRegister.setBackground(Color.decode(backGroundColor));
        btnRegister.setFocusable(false);
        btnRegister.addActionListener(e -> {
            RegisterFrame reg = new RegisterFrame();
            reg.setVisible(true);
            dispose();
        });

        btnRegister.setFont(new Font(textType, Font.PLAIN, 14));
        btnRegister.setBounds(312, 562, 183, 33);
        contentPane.add(btnRegister);

        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setFont(new Font(textType, Font.PLAIN, 18));
        lblPhone.setBounds(198, 311, 83, 33);
        contentPane.add(lblPhone);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font(textType, Font.PLAIN, 18));
        lblPassword.setBounds(198, 355, 83, 33);
        contentPane.add(lblPassword);

        JButton btnNewButton = new JButton("Forgot password?");
        btnNewButton.setBorder(null);
        btnNewButton.setBackground(Color.decode(backGroundColor));
        btnNewButton.setFocusable(false);
        btnNewButton.setFont(new Font(textType, Font.PLAIN, 14));
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ForgottenKeyFrame Fkey = new ForgottenKeyFrame();
                Fkey.setVisible(true);
                dispose();
            }
        });
        btnNewButton.setBounds(312, 518, 183, 33);

        contentPane.add(btnNewButton);
    }

    public static void updateCheckBoxFromFile(String filePath, JCheckBox checkBox) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String remember = reader.readLine();
            if ("1".equals(remember)) {
                checkBox.setSelected(true);
            } else if ("0".equals(remember)) {
                checkBox.setSelected(false);
            }
        } catch (IOException e) {
            //------------ File does not exist or can not be opened
        }
    }

    public static void loadUserInfo(String filePath, JTextField textFieldPhone, JPasswordField passwordField) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String phone = reader.readLine();
            String password = reader.readLine();

            if (phone != null) {
                textFieldPhone.setText(phone);
            }

            if (password != null) {
                passwordField.setText(password);
            }
        } catch (IOException e) {
            System.out.println("Error while reading...: " + e.getMessage());
        }
    }

    private boolean authenticateUser(String phone, String password) {
        try {
            URL url = new URL(urlPathLogin);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Prepare JSON data
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msisdn", phone);
            jsonObject.put("password", password);

            // Send POST request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonObject.toString().getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            String responseMessage;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    responseMessage = response.toString();
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(responseMessage);
                    String statusCode = jsonResponse.optString("statusCode", "Unknown");
                    System.out.println("Status code: " + statusCode); // Print status code

                    if ("OK".equals(statusCode)) {
                        MainFrame mainFrame = new MainFrame(phone);
                        mainFrame.setVisible(true);
                        dispose();
                        return true; // Successful authentication
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong information.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                        textFieldPhone.setText("");
                        passwordField.setText("");
                        return false; // Authentication failed
                    }
                } catch (IOException e) {
                    responseMessage = "Error reading success stream: " + e.getMessage();
                    System.out.println(responseMessage);
                    return false;
                }
            } else {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    responseMessage = response.toString();
                    System.out.println("Error response: " + responseMessage);

                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(responseMessage);
                    String statusCode = jsonResponse.optString("statusCode", "Unknown");
                    System.out.println("Status code: " + statusCode); // Print status code

                    return false; // Authentication failed
                } catch (IOException e) {
                    responseMessage = "Error reading error stream: " + e.getMessage();
                    System.out.println(responseMessage);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public class WriteTXT {
        public void bool(String[] args, String value) {
            File file = new File(rememberMePath);
            try {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(value);
                }
            } catch (IOException e) {
                System.out.println("Error while writing...: " + e.getMessage());
            }
        }

        public void userInfo(String phone, String password) {
            File file = new File(InfoPath);
            try {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(phone + "\n" + password);
                }
            } catch (IOException e) {
                System.out.println("Error while writing...: " + e.getMessage());
            }
        }
    }
}
