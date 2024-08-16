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
import java.io.*;
import org.json.JSONObject;
import org.json.JSONException;

public class ForgottenKeyFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField txtWarning;

    private String urlPathReset = ConfigLoader.getProperty("urlPathReset");
    private String gifPath = ConfigLoader.getProperty("gifPath");
    private String logoPath = ConfigLoader.getProperty("logoPath");
    private String textType = ConfigLoader.getProperty("textType");
    private String goBackLogoPath = ConfigLoader.getProperty("goBackIconPath");
    private String backgroundColor = ConfigLoader.getProperty("backGroundColor");
    private String foreGroundColor = ConfigLoader.getProperty("foreGroundColor");

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ForgottenKeyFrame frame = new ForgottenKeyFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ForgottenKeyFrame() {
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

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon(getClass().getResource(logoPath)));
        lblNewLabel.setBounds(0, 0, 459, 436);
        panel.add(lblNewLabel);

        setContentPane(contentPane);

        textField = new JTextField();
        textField.setBorder(null);
        textField.setBackground(Color.decode(backgroundColor));
        textField.setForeground(Color.decode(foreGroundColor));
        textField.setFont(new Font(textType, Font.PLAIN, 18));
        textField.setBounds(137, 287, 272, 42);
        contentPane.add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setBackground(Color.decode(backgroundColor));
        textField_1.setForeground(Color.decode(foreGroundColor));
        textField_1.setBorder(null);
        textField_1.setFont(new Font(textType, Font.PLAIN, 18));
        textField_1.setBounds(137, 372, 272, 42);
        contentPane.add(textField_1);
        textField_1.setColumns(10);

        txtWarning = new JTextField();
        txtWarning.setVisible(false);
        txtWarning.setBackground(SystemColor.control);
        txtWarning.setForeground(Color.RED);
        txtWarning.setFont(new Font(textType, Font.PLAIN, 20));
        txtWarning.setBorder(null);
        txtWarning.setEditable(false);
        txtWarning.setBounds(38, 555, 432, 42);
        contentPane.add(txtWarning);
        txtWarning.setColumns(10);

        JButton btnNewButton1 = new JButton("Send Recovery Email");
        btnNewButton1.setFocusable(false);
        btnNewButton1.addActionListener(e -> {
            if (!(textField_1.getText().isBlank() || textField.getText().isBlank())) {
                sendRecoveryRequest(textField.getText(), textField_1.getText());
            } else {
                txtWarning.setVisible(true);
                txtWarning.setForeground(Color.RED);
                txtWarning.setText("You must fill all of the blanks for registration !!!");
            }
        });

        btnNewButton1.setForeground(Color.WHITE);
        btnNewButton1.setFont(new Font(textType, Font.PLAIN, 20));
        btnNewButton1.setBounds(116, 447, 260, 53);
        btnNewButton1.setBackground(Color.decode(foreGroundColor));
        contentPane.add(btnNewButton1);

        JLabel lblNewLabel_1 = new JLabel("National ID:");
        lblNewLabel_1.setFont(new Font(textType, Font.PLAIN, 18));
        lblNewLabel_1.setBounds(26, 287, 101, 42);
        contentPane.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Email:");
        lblNewLabel_2.setFont(new Font(textType, Font.PLAIN, 18));
        lblNewLabel_2.setBounds(26, 372, 101, 42);
        contentPane.add(lblNewLabel_2);

        JButton btnNewButton = new JButton("");
        btnNewButton.addActionListener(e -> {
            Login log = new Login();
            log.setVisible(true);
            dispose();
        });

        btnNewButton.setBorder(null);
        btnNewButton.setBackground(SystemColor.control);
        btnNewButton.setIcon(new ImageIcon(getClass().getResource(goBackLogoPath)));
        btnNewButton.setBounds(817, 11, 42, 42);
        contentPane.add(btnNewButton);
    }

    private void sendRecoveryRequest(String nationalId, String email) {
        new Thread(() -> {
            try {
                URL url = new URL(urlPathReset);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "*/*");
                conn.setDoOutput(true);

                // Create JSON payload
                String jsonInputString = String.format("{\"email\": \"%s\", \"TCNumber\": \"%s\"}", email, nationalId);

                // Send JSON payload
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read response
                InputStream is = conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST
                    ? conn.getErrorStream()
                    : conn.getInputStream();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    parseResponse(response.toString(), conn.getResponseCode());
                }

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    txtWarning.setVisible(true);
                    txtWarning.setForeground(Color.RED);
                    txtWarning.setText("Error occurred: " + e.getMessage());
                });
            }
        }).start();
    }

    private void parseResponse(String responseBody, int statusCode) {
        SwingUtilities.invokeLater(() -> {
            if (statusCode == HttpURLConnection.HTTP_OK) {
                txtWarning.setVisible(true);
                txtWarning.setForeground(Color.GREEN);
                txtWarning.setText("Recovery Mail has been sent.");
                Login lg = new Login();
                lg.setVisible(true);
                dispose();
            } else {
                // Extract the status code from the response
                String statusCodeStr = "Unknown";
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    statusCodeStr = jsonResponse.getString("statusCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txtWarning.setVisible(true);
                txtWarning.setForeground(Color.RED);
                txtWarning.setText("Error: " + statusCodeStr);
            }
        });
    }
}
