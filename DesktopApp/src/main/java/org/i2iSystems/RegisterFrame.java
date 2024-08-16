package org.i2iSystems;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;

public class RegisterFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textFieldName;
    private JTextField textFieldSurname;
    private JTextField textFieldNationalId;
    private JTextField textFieldPassword;
    private JTextField textFieldPhone;
    private JTextField textFieldEmail;
    private JComboBox<String> comboBoxTariff;
    private JTextField txtWarning;

    private String gifPath = ConfigLoader.getProperty("gifPath");
    private String logoPath = ConfigLoader.getProperty("logoPath");
    private String textType = ConfigLoader.getProperty("textType");
    private String backGroundColor = ConfigLoader.getProperty("backGroundColor");
    private String foreGroundColor = ConfigLoader.getProperty("foreGroundColor");
    private String goBackIconPath = ConfigLoader.getProperty("goBackIconPath");
    private String urlPathRegister = ConfigLoader.getProperty("urlPathRegister");

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                RegisterFrame frame = new RegisterFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public RegisterFrame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 885, 716);
        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;
            private Image backgroundImage;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }

            {
                backgroundImage = new ImageIcon(getClass().getResource(gifPath)).getImage();
            }
        };
        contentPane.setBorder(null);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(409, 108, 432, 436);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblLogo = new JLabel("");
        lblLogo.setIcon(new ImageIcon(getClass().getResource(logoPath)));
        lblLogo.setBounds(0, 0, 459, 436);
        panel.add(lblLogo);

        setContentPane(contentPane);

        createFormFields();
        createTariffComboBox();
        createRegisterButton();
        createBackButton();
        addLabels();
    }

    private void createFormFields() {

        textFieldName = createTextField(137, 108);
        textFieldSurname = createTextField(137, 174);
        textFieldNationalId = createTextField(137, 240);
        textFieldPassword = createTextField(137, 306);
        textFieldPhone = createTextField(137, 372);
        textFieldEmail = createTextField(137, 504);

        txtWarning = new JTextField();
        txtWarning.setVisible(false);
        txtWarning.setBackground(SystemColor.control);
        txtWarning.setForeground(Color.RED);
        txtWarning.setFont(new Font(textType, Font.PLAIN, 20));
        txtWarning.setBorder(null);
        txtWarning.setEditable(false);
        txtWarning.setBounds(39, 635, 432, 42);
        contentPane.add(txtWarning);
    }

    private JTextField createTextField(int x, int y) {
        JTextField textField = new JTextField();
        textField.setBorder(null);
        textField.setBackground(Color.decode(backGroundColor));
        textField.setForeground(Color.decode(foreGroundColor));
        textField.setFont(new Font(textType, Font.PLAIN, 18));
        textField.setBounds(x, y, 272, 42);
        contentPane.add(textField);
        return textField;
    }

    private void createTariffComboBox() {
        String[] packets = {
            "", "MERCURY: 1500Mn 3GB 1000SMS 50TL",
            "MARS: 1500Mn 5GB 1000SMS 100TL",
            "EARTH: 2000Mn 7GB 1500SMS 120TL",
            "NEPTUNE: 2000Mn 10GB 1500SMS 150TL",
            "SATURN: 2000Mn 15GB 2000SMS 180TL",
            "JUPITER: 2500Mn 20GB 5000SMS 225TL"
        };
        comboBoxTariff = new JComboBox<>(packets);
        comboBoxTariff.setFocusable(false);
        comboBoxTariff.setBorder(null);
        comboBoxTariff.setFont(new Font(textType, Font.PLAIN, 15));
        comboBoxTariff.setBackground(Color.decode(backGroundColor));
        comboBoxTariff.setForeground(Color.decode(foreGroundColor));
        comboBoxTariff.setBounds(137, 438, 307, 42);
        contentPane.add(comboBoxTariff);
    }

    private void createRegisterButton() {
        JButton btnRegister = new JButton("Register");
        btnRegister.setFocusable(false);
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (areFieldsNotEmpty()) {
                    String selectedPacket = comboBoxTariff.getSelectedItem().toString();
                    if (!selectedPacket.equals("")) {
                        txtWarning.setText("");
                        registerUser();
                    } else {
                        txtWarning.setText("Please, choose a tariff!");
                    }
                } else {
                    txtWarning.setVisible(true);
                    txtWarning.setText("You must fill all the blanks!!!");
                }
            }
        });
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font(textType, Font.PLAIN, 20));
        btnRegister.setBounds(147, 582, 146, 53);
        btnRegister.setBackground(Color.decode(foreGroundColor));
        contentPane.add(btnRegister);
    }

    private void createBackButton() {
        JButton btnBack = new JButton("");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Login log = new Login();
                log.setVisible(true);
                dispose();
            }
        });
        btnBack.setBorder(null);
        btnBack.setBackground(SystemColor.control);
        btnBack.setIcon(new ImageIcon(getClass().getResource(goBackIconPath)));
        btnBack.setBounds(817, 11, 42, 42);
        contentPane.add(btnBack);
    }

    private void addLabels() {
        addLabel("Name:", 18, 108);
        addLabel("Surname:", 18, 174);
        addLabel("National ID:", 18, 240);
        addLabel("Password:", 18, 306);
        addLabel("Phone:", 18, 372);
        addLabel("Tariff:", 18, 438);
        addLabel("Email:", 18, 504);
    }

    private void addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(text, Font.PLAIN, 18));
        label.setBounds(x, y, 101, 42);
        contentPane.add(label);
    }

    private boolean areFieldsNotEmpty() {
        return !(
            textFieldName.getText().isEmpty() ||
            textFieldSurname.getText().isEmpty() ||
            textFieldNationalId.getText().isEmpty() ||
            textFieldPassword.getText().isEmpty() ||
            textFieldPhone.getText().isEmpty() ||
            textFieldEmail.getText().isEmpty()
        );
    }

    private void registerUser() {
        try {
            URL url = new URL(urlPathRegister);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String selectedItem = (String) comboBoxTariff.getSelectedItem();
            int colonIndex = selectedItem.indexOf(':');
            String result = "EVRENCELL " + selectedItem.substring(0, colonIndex).trim();

            String jsonInputString = String.format(
                "{\"name\": \"%s\", \"surname\": \"%s\", \"msisdn\": \"%s\", \"email\": \"%s\", \"password\": \"%s\", \"TCNumber\": \"%s\", \"packageName\": \"%s\"}",
                textFieldName.getText(),
                textFieldSurname.getText(),
                textFieldPhone.getText(),
                textFieldEmail.getText(),
                textFieldPassword.getText(),
                textFieldNationalId.getText(),
                result
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                (responseCode == HttpURLConnection.HTTP_OK) ? connection.getInputStream() : connection.getErrorStream()
            ));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Response Code: " + responseCode);
            System.out.println("Response: " + response.toString());

            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                Login log = new Login();
                log.setVisible(true);
                dispose();
            } else {
                txtWarning.setText("Registration Failed. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            txtWarning.setText("An error occurred. Please try again.");
        }
    }
}
