package com.sk.i2i.smsapp.ui;

import com.sk.i2i.smsapp.handler.IHttpRequestHandler;
import com.sk.i2i.smsapp.model.UserInfo;
import com.sk.i2i.smsapp.parser.IResponseParser;
import com.sk.i2i.smsapp.formatter.IMessageFormatter;
import com.sk.i2i.smsapp.validator.IInputValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputWindow {
    private JTextField inputField;
    private JButton sendButton;
    private JPanel chatPanel;
    private JFrame frame;
    private JScrollPane scrollPane;

    private IInputValidator validator;
    private IHttpRequestHandler httpRequestHandler;
    private IResponseParser responseParser;
    private IMessageFormatter messageFormatter;

    private static final Logger logger = LogManager.getLogger(InputWindow.class);

    public InputWindow(IInputValidator validator, IHttpRequestHandler httpRequestHandler, IResponseParser responseParser, IMessageFormatter messageFormatter) {
        this.validator = validator;
        this.httpRequestHandler = httpRequestHandler;
        this.responseParser = responseParser;
        this.messageFormatter = messageFormatter;

        // Frame oluşturma
        frame = new JFrame("EvrenCell SMS App");
        inputField = new JTextField(20);
        sendButton = new JButton("Send");

        // Chat paneli oluşturma
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        // Scroll pane
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Send button event listener
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Enter tuşuna basıldığında mesaj gönderme
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        // Layout düzenlemesi
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Mesaj gönderme işlemini kapsayan metot
    private void sendMessage() {
        String input = inputField.getText();
        appendMessage("You: " + input, true);
        handleInput(input);
        inputField.setText("");
    }

    // Mesajları chat alanına eklemek için metot
    public void appendMessage(String message, boolean isUser) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT));

        JTextArea messageLabel = new JTextArea(message);
        messageLabel.setWrapStyleWord(true);
        messageLabel.setLineWrap(true);
        messageLabel.setEditable(false);
        messageLabel.setBackground(isUser ? new Color(173, 216, 230) : new Color(240, 240, 240));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Mesaj kutucuğu için boyut ve hizalama
        int width = chatPanel.getWidth() - 100; // Maksimum genişlik, sola ve sağa biraz boşluk bırak
        messageLabel.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));

        messagePanel.add(messageLabel);
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createVerticalStrut(10)); // Mesajlar arasında boşluk

        chatPanel.revalidate();
        scrollToBottom();
        frame.repaint();
    }

    // Giriş işleme metodu
    private void handleInput(String input) {
        if (validator.validate(input)) {
            String phoneNumber = input.split(" ")[1];

            try {
                String response = httpRequestHandler.sendGetRequest(phoneNumber);
                UserInfo userInfo = responseParser.parse(response);
                String message = messageFormatter.format(userInfo);
                appendMessage("Evren Cell: " + message, false);
            } catch (Exception e) {
                logger.error("Bir hata oluştu: " + e.getMessage(), e);
                appendMessage("Evren Cell: Bir hata oluştu, lütfen daha sonra tekrar deneyin.", false);
            }
        } else {
            appendMessage("Evren Cell: Hatalı mesaj gönderdiniz. Doğru format:'KALAN 5359658072'", false);
        }
    }

    // Otomatik olarak en alta kaydırma metodu
    private void scrollToBottom() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        });
    }
}
