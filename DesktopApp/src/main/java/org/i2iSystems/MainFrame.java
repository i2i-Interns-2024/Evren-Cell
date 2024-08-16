package org.i2iSystems;

import javax.swing.*;
import java.awt.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.event.*;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField1; //KALAN DATA %
    private JTextField textField2; //KALAN MINUTE %
    private JTextField textField3; //KALAN SMS %
    private JTextField textField4; //DATA YAZISI
    private JTextField textField5; //MINUTE YAZISI
    private JTextField textField6; //SMS YAZISI
    private JTextField textField7; //DATA END DATE
    private JTextField textField8; //MINUTE END DATE
    private JTextField textField9; //SMS END DATE
    private JTextField textField10;
    private int angle = 360;


    private static String phone;
    private String textType = ConfigLoader.getProperty("textType");
    private String gifPath = ConfigLoader.getProperty("gifPath");
    private String logoPath = ConfigLoader.getProperty("logoPath");
    private String urlPathBalance = ConfigLoader.getProperty("urlPathBalance");
    private String urlPathRemain = ConfigLoader.getProperty("urlPathRemain");
    private String backGroundColor = ConfigLoader.getProperty("backGroundColor");
    private String foreGroundColor = ConfigLoader.getProperty("foreGroundColor");
    private String InfoPath = ConfigLoader.getProperty("InfoPath");

    private int firstRatio;
    private int secondRatio;
    private int thirdRatio;

        // Timer to refresh the frame every 1 second
    private Timer refreshTimer;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame(phone);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainFrame(String phone) {
        this.phone = phone;
        textField1 = new JTextField();
        textField2 = new JTextField();
        textField3 = new JTextField();
        textField4 = new JTextField();
        textField5 = new JTextField();
        textField6 = new JTextField();
        textField7 = new JTextField();
        textField8 = new JTextField();
        textField9 = new JTextField();
        textField10 = new JTextField();
        textField10 = new JTextField();

        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();

        RoundedPanel drawingPanel1 = new RoundedPanel(20, 95);
        RoundedPanel drawingPanel2 = new RoundedPanel(20, 95);
        RoundedPanel drawingPanel3 = new RoundedPanel(20, 95);

        fetchAndPrintBalance(phone,textField1,textField2,textField3,label1,label2,label3, drawingPanel1, drawingPanel2, drawingPanel3);

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 885, 698);
        contentPane = new JPanel() {
            /**
			 *
			 */
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

        JButton btnLogOut = new JButton("Log Out");
        btnLogOut.setFocusable(false);
        btnLogOut.setBackground(Color.decode(backGroundColor));
        btnLogOut.setForeground(Color.decode(foreGroundColor));
        btnLogOut.setBorder(null);
        btnLogOut.setBounds(735, 25, 106, 40);
        btnLogOut.addActionListener(e -> {
        	clearFileContent(InfoPath);
            Login log = new Login();
            log.setVisible(true);
            dispose();
        });
        contentPane.setLayout(null);
        btnLogOut.setFont(new Font(textType, Font.PLAIN, 16));
        contentPane.add(btnLogOut);

        JPanel panel = new JPanel();
        panel.setBounds(409, 108, 432, 436);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon(getClass().getResource(logoPath)));
        lblNewLabel.setBounds(-37, 0, 459, 436);
        panel.add(lblNewLabel);

        // Create three custom drawing panels with rounded corners
        float ratio = Float.parseFloat(removePercentageSign(textField1.getText()));
        double ratio2 = ratio * 3.6;


        drawingPanel1.setBounds(50, 80, 232, 150);
        drawingPanel1.setBackground(new Color(200, 184, 232));
        contentPane.add(drawingPanel1);
        drawingPanel1.setLayout(null);


        textField1.setRequestFocusEnabled(false);
        textField1.setEditable(false);
        textField1.setBackground(Color.WHITE);
        textField1.setBounds(40, 55, 58, 35);
        textField1.setBorder(null);
        textField1.setFont(new Font(textType, Font.PLAIN, 19));
        drawingPanel1.add(textField1);

        ratio = Float.parseFloat(removePercentageSign(textField2.getText()));
        ratio2 = ratio * 3.6;



        drawingPanel2.setBounds(50, 250, 232, 150);
        drawingPanel2.setBackground(new Color(200, 184, 232));
        contentPane.add(drawingPanel2);
        drawingPanel2.setLayout(null);


        textField2.setRequestFocusEnabled(false);
        textField2.setEditable(false);
        textField2.setBackground(Color.WHITE);
        textField2.setBorder(null);
        textField2.setFont(new Font(textType, Font.PLAIN, 19));
        textField2.setBounds(40, 55, 58, 35);
        textField2.setColumns(10);
        drawingPanel2.add(textField2);


        ratio = Float.parseFloat(removePercentageSign(textField2.getText()));
        ratio2 = ratio * 3.6;


        drawingPanel3.setBounds(50, 420, 232, 150);
        drawingPanel3.setBackground(new Color(200, 184, 232));
        contentPane.add(drawingPanel3);
        drawingPanel3.setLayout(null);


        textField3.setRequestFocusEnabled(false);
        textField3.setEditable(false);
        textField3.setBackground(Color.WHITE);
        textField3.setBorder(null);
        textField3.setFont(new Font(textType, Font.PLAIN, 19));
        textField3.setBounds(40, 55, 58, 35);
        drawingPanel3.add(textField3);
        textField3.setColumns(10);
//----------------------------------------------------------------------------------   Data tuşu
        textField4 = new JTextField();
        textField4.setRequestFocusEnabled(false);
        textField4.setEditable(false);
        textField4.setBackground(Color.WHITE);
        textField4.setBounds(150, 20, 58, 35);
        textField4.setBorder(null);
        textField4.setText("Data");
        textField4.setFont(new Font(textType, Font.PLAIN, 24));
        textField4.setForeground(Color.decode(foreGroundColor));
        textField4.setBackground(Color.decode(backGroundColor));
        drawingPanel1.add(textField4);

        RoundedPanel drawingPanel4 = new RoundedPanel(20, 1);
        drawingPanel2.setBounds(50, 250, 232, 150);
        drawingPanel2.setBackground(new Color(200, 184, 232));
        contentPane.add(drawingPanel4);
        drawingPanel2.setLayout(null);

//---------------------------------------------------------------------------------- Minute tuşu
        textField5.setRequestFocusEnabled(false);
        textField5.setEditable(false);
        textField5.setBackground(Color.WHITE);
        textField5.setBounds(140, 20, 700, 35); // Position for the first text field
        textField5.setBorder(null);
        textField5.setText("Minute");
        textField5.setFont(new Font(textType, Font.PLAIN, 24));
        textField5.setBackground(Color.decode(backGroundColor));
        textField5.setForeground(Color.decode(foreGroundColor));
        drawingPanel2.add(textField5);

        RoundedPanel drawingPanel5 = new RoundedPanel(20, angle); // 20 is the corner radius
        drawingPanel5.setBounds(50, 250, 232, 150); // Position for the second panel
        drawingPanel5.setBackground(new Color(200, 184, 232));
        contentPane.add(drawingPanel5);
        drawingPanel5.setLayout(null);
//----------------------------------------------------------------------------------  SMS tuşu
        textField6.setRequestFocusEnabled(false);
        textField6.setEditable(false);
        textField6.setBackground(Color.WHITE);
        textField6.setBounds(150, 20, 58, 35);
        textField6.setBorder(null);
        textField6.setText("SMS");
        textField6.setFont(new Font(textType, Font.PLAIN, 24));
        textField6.setBackground(Color.decode(backGroundColor));
        textField6.setForeground(Color.decode(foreGroundColor));
        drawingPanel3.add(textField6);

        RoundedPanel drawingPanel6 = new RoundedPanel(20, angle);
        drawingPanel5.setBounds(50, 250, 232, 150);
        drawingPanel5.setBackground(new Color(200, 184, 232));
        contentPane.add(drawingPanel6);
        drawingPanel5.setLayout(null);
//---------------------------------------------------------------------------------- Data end date

        textField7.setRequestFocusEnabled(false);
        textField7.setEditable(false);
        textField7.setBackground(Color.WHITE);
        textField7.setBounds(150, 70, 58, 50); // Position for the first text field
        textField7.setBorder(null);
        textField7.setFont(new Font(textType, Font.PLAIN, 12));
        textField7.setBackground(Color.decode(backGroundColor));
        textField7.setForeground(Color.decode(foreGroundColor));
        drawingPanel1.add(textField7);

        RoundedPanel drawingPanel7 = new RoundedPanel(20, angle);
        drawingPanel7.setBounds(50, 250, 232, 150);
        drawingPanel7.setBackground(new Color(200, 184, 232));
        drawingPanel7.setLayout(null);
        contentPane.add(drawingPanel7);

      //---------------------------------------------------------------------------------- Minute end date

        textField8.setRequestFocusEnabled(false);
        textField8.setEditable(false);
        textField8.setBackground(Color.WHITE);
        textField8.setBounds(150, 70, 58, 50); // Position for the first text field
        textField8.setBorder(null);
        textField8.setFont(new Font(textType, Font.PLAIN, 12));
        textField8.setBackground(Color.decode(backGroundColor));
        textField8.setForeground(Color.decode(foreGroundColor));
        drawingPanel2.add(textField8);

        RoundedPanel drawingPanel8 = new RoundedPanel(20, angle);
        drawingPanel8.setBounds(50, 250, 232, 150); // Define correct position and size
        drawingPanel8.setBackground(new Color(200, 184, 232));
        drawingPanel8.setLayout(null);
        contentPane.add(drawingPanel8);

        textField9.setRequestFocusEnabled(false);
        textField9.setEditable(false);
        textField9.setBackground(Color.WHITE);
        textField9.setBounds(150, 70, 58, 50); // Position for the first text field
        textField9.setBorder(null);
        textField9.setFont(new Font(textType, Font.PLAIN, 12));
        textField9.setBackground(Color.decode(backGroundColor));
        textField9.setForeground(Color.decode(foreGroundColor));
        drawingPanel3.add(textField9);

        RoundedPanel drawingPanel9 = new RoundedPanel(20, angle);
        drawingPanel9.setBounds(50, 250, 232, 150); // Define correct position and size
        drawingPanel9.setBackground(new Color(200, 184, 232));
        drawingPanel9.setLayout(null);
        contentPane.add(drawingPanel9);

        textField10.setBorder(null);
        textField10.setBackground(SystemColor.control);
        textField10.setBounds(409, 25, 297, 40);
        contentPane.add(textField10);
        textField10.setColumns(10);
        textField10.setForeground(Color.decode(foreGroundColor));
        textField10.setEditable(false);
        textField10.setFont(new Font(textType, Font.PLAIN, 30));

        JLabel lblNewLabel_1 = new JLabel("Calculating...");
        lblNewLabel_1.setFont(new Font(textType, Font.PLAIN, 14));
        lblNewLabel_1.setBounds(113, 227, 300, 26);
        lblNewLabel_1.setForeground(Color.decode(foreGroundColor));
        lblNewLabel_1.setBackground(Color.decode(backGroundColor));
        contentPane.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Calculating...");
        lblNewLabel_2.setFont(new Font(textType, Font.PLAIN, 14));
        lblNewLabel_2.setBounds(113, 396, 300, 26);
        lblNewLabel_2.setForeground(Color.decode(foreGroundColor));
        lblNewLabel_2.setBackground(Color.decode(backGroundColor));
        contentPane.add(lblNewLabel_2);

        JLabel lblNewLabel_3 = new JLabel("Calculating...");
        lblNewLabel_3.setFont(new Font(textType, Font.PLAIN, 14));
        lblNewLabel_3.setBounds(113, 565, 300, 26);
        lblNewLabel_3.setForeground(Color.decode(foreGroundColor));
        lblNewLabel_3.setBackground(Color.decode(backGroundColor));
        contentPane.add(lblNewLabel_3);

        setContentPane(contentPane);

        // Set the timer to refresh the frame every 1 second (1000 milliseconds)
        refreshTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchAndPrintBalance(phone, textField1, textField2, textField3, lblNewLabel_1,lblNewLabel_2,lblNewLabel_3,drawingPanel1,drawingPanel2,drawingPanel3);
            }
        });
        refreshTimer.start();
    }

    public static void clearFileContent(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("");
        } catch (IOException e) {
            //System.out.println("Dosya yazma hatası: " + e.getMessage());
        }
    }
    public void fetchAndPrintBalance(String phoneNumber, JTextField textField1, JTextField textField2, JTextField textField3, JLabel label1,JLabel label2,JLabel label3, RoundedPanel d1, RoundedPanel d2, RoundedPanel d3) {
        String urlString = urlPathBalance + phoneNumber;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String msisdn = jsonResponse.getString("msisdn");
            int balanceData = jsonResponse.getInt("balanceData");
            int balanceSms = jsonResponse.getInt("balanceSms");
            int balanceMinutes = jsonResponse.getInt("balanceMinutes");
            String sdate = jsonResponse.getString("sdate");
            String edate = jsonResponse.getString("edate");

            float data_ratio = calculateRatio(balanceData,fetchPackageDetails4int(phoneNumber, "amountData"));
            float minute_ratio = calculateRatio(balanceMinutes, fetchPackageDetails4int(phoneNumber, "amountMinutes"));
            float sms_ratio = calculateRatio(balanceSms, fetchPackageDetails4int(phoneNumber, "amountSms"));

            d1.setRatio((int)(3.6*data_ratio));
            d2.setRatio((int)(3.6*minute_ratio));
            d3.setRatio((int)(3.6*sms_ratio));
//            textField1.setText("%" + dataRatioStr);
//            textField2.setText("%" + minuteRatioStr);
//            textField3.setText("%" + smsRatioStr);

//            calculateAll(1, fetchAndPrintPackageDetails(phoneNumber), balanceData);
//            calculateAll(2, fetchAndPrintPackageDetails(phoneNumber), balanceMinutes);
//            calculateAll(3, fetchAndPrintPackageDetails(phoneNumber), balanceSms);

            CalculateAllRemastered(1, balanceData);
            CalculateAllRemastered(2, balanceMinutes);
            CalculateAllRemastered(3, balanceSms);

            label1.setText(String.valueOf(balanceData)+" MB LEFT");
            label2.setText(String.valueOf(balanceMinutes)+" Minutes  LEFT");
            label3.setText(String.valueOf(balanceSms)+" SMS LEFT");

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static String formatDateString(String inputDate) {
        if (inputDate.length() >= 10) {
            return inputDate.substring(2, 10);
        } else {
            return "Input string is too short";
        }
    }
    public static String removePercentageSign(String text) {
    if (text == null) {
        return null;
    }

    // Replace commas with dots for float parsing
    text = text.replace(',', '.');

    // Remove `%` sign if present
    if (text.startsWith("%")) {
        return text.substring(1);
    }

    return text;
}

    public float calculateRatio(float left, float tam) {
        return  (left / tam) * 100;
    }
    public void CalculateAllRemastered(int mod, int left) {
    try {
        String urlString = urlPathRemain + phone;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "*/*");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        String packageName = jsonResponse.getString("packageName");

        int amountMinutes = jsonResponse.getInt("amountMinutes");
        int amountData = jsonResponse.getInt("amountData");
        int amountSms = jsonResponse.getInt("amountSms");

        int dataRatio = (int)calculateRatio(left,amountData);
        int minuteRatio = (int)calculateRatio(left,amountMinutes);
        int smsRatio = (int)calculateRatio(left, amountSms);

        float dataRatio11 = calculateRatio(left,amountData);
        float dataRatio22 = calculateRatio(left,amountMinutes);
        float dataRatio33 = calculateRatio(left,amountSms);

        // Format to keep only the first digit after the decimal point
        String strDataRatio11 = String.format("%.1f", dataRatio11);
        String strDataRatio22 = String.format("%.1f", dataRatio22);
        String strDataRatio33 = String.format("%.1f", dataRatio33);

        if (mod == 1) { // Data
            if(dataRatio == 100){
                textField1.setText("%"+String.valueOf(dataRatio));
            }
            else{
                textField1.setText("%"+strDataRatio11);
            }
        } else if (mod == 2) { // Minute
            if(minuteRatio == 100){
                textField2.setText("%"+String.valueOf(minuteRatio));
            }
            else{
                textField2.setText("%"+strDataRatio22);
            }

        } else if (mod == 3) { // SMS
            if(smsRatio == 100){
                textField3.setText("%"+String.valueOf(smsRatio));
            }
            else{
                textField3.setText("%"+strDataRatio33);
            }

        }
        textField10.setText(packageName);


    } catch (Exception e) {
        e.printStackTrace();
    }

}
    public int fetchPackageDetails4int(String phoneNumber, String mod) {
        String urlString = urlPathRemain + phoneNumber;
        int result = 0;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());

            if (jsonResponse.has(mod)) {
                Object value = jsonResponse.get(mod);

                if (value instanceof String) {
                    result = Integer.parseInt((String) value);
                } else if (value instanceof Integer) {
                    result = (Integer) value;
                } else if (value instanceof Double) {
                    result = ((Double) value).intValue();
                } else {
                    throw new JSONException("Unsupported data type for key: " + mod);
                }
            } else {
                System.out.println("Key not found in JSON response.");
            }

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
    public String fetchAndPrintPackageDetails(String phoneNumber) {
        String urlString = urlPathRemain + phoneNumber;
        String packageName = "Unknown"; // Varsayılan değer

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            packageName = jsonResponse.getString("packageName");
            textField10.setText(packageName);

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return packageName;
    }
}

class RoundedPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private int cornerRadius;
    private int ratio;

    public void setRatio(int ratio){
        this.ratio = ratio;
    }


    public RoundedPanel(int radius, int ratio) {
        this.cornerRadius = radius;
        this.ratio = ratio;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        drawCircles(g2d);
    }

    private void drawCircles(Graphics2D g2d) {
        // Get panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Set circle dimensions
        int circleDiameter = 100;
        int circleX = (panelWidth - circleDiameter) / 2 - 50; // X position to center the circle
        int circleY = (panelHeight - circleDiameter) / 2; // Y position to center the circle

        // Define the color with hex code #c8b8e8
        Color ringColor = Color.decode(ConfigLoader.getProperty("foreGroundColor"));
        Color ringLeftColor = Color.decode(ConfigLoader.getProperty("backGroundColor"));
        // Draw the ring
        g2d.setColor(ringColor);


        drawPartialCircle(g2d, circleX, circleY, circleDiameter, ringColor, ratio); // Draw partial ring

        // Draw the smaller circle
        g2d.setColor(Color.WHITE);
        int smallerCircleDiameter = circleDiameter - 20; // Adjust size as needed
        int smallerCircleX = circleX + (circleDiameter - smallerCircleDiameter) / 2; // Center the smaller circle
        int smallerCircleY = circleY + (circleDiameter - smallerCircleDiameter) / 2; // Center the smaller circle
        g2d.drawOval(smallerCircleX, smallerCircleY, smallerCircleDiameter, smallerCircleDiameter); // Draw outline of the smaller circle
        g2d.fillOval(smallerCircleX, smallerCircleY, smallerCircleDiameter, smallerCircleDiameter); // Fill the smaller circle with color
    }

    private void drawPartialCircle(Graphics2D g2d, int x, int y, int diameter, Color color, int angle) {
        g2d.setColor(color);
        g2d.fillArc(x, y, diameter, diameter, 90, angle); // Start at the top of the circle
    }
}


