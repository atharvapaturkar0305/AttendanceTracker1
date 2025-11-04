package com.attendancetracker;

import com.formdev.flatlaf.FlatLightLaf;
import com.attendancetracker.view.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set modern look and feel
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Set default font
            UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
            
            // Start the application
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error initializing application: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 