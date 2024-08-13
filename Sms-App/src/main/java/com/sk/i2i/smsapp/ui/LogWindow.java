package com.sk.i2i.smsapp.ui;

import javax.swing.*;
import java.awt.*;

public class LogWindow {
    private JTextArea logArea;
    private JFrame frame;

    public LogWindow() {
        frame = new JFrame("Log Window");
        logArea = new JTextArea(20, 50);
        logArea.setEditable(false);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(logArea), BorderLayout.CENTER);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void appendLog(String log) {
        logArea.append(log + "\n");
    }
}
