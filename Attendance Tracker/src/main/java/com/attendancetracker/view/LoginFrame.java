package com.attendancetracker.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.attendancetracker.model.User;
import com.attendancetracker.service.AuthenticationService;
import com.formdev.flatlaf.FlatClientProperties;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private final AuthenticationService authService;

    public LoginFrame() {
        authService = new AuthenticationService();
        
        setTitle("Attendance Tracker - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        formPanel.add(passwordField, gbc);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        formPanel.add(roleComboBox, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 120, 212));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(loginButton, gbc);

        // Register link
        gbc.gridy = 4;
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel registerLabel = new JLabel("Don't have an account?");
        JButton registerButton = new JButton("Register");
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(new Color(0, 120, 212));
        registerPanel.add(registerLabel);
        registerPanel.add(registerButton);
        formPanel.add(registerPanel, gbc);
        
        // Forgot Password link
        gbc.gridy = 5;
        JPanel forgotPasswordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(new Color(0, 120, 212));
        forgotPasswordPanel.add(forgotPasswordButton);
        formPanel.add(forgotPasswordPanel, gbc);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> showRegistrationFrame());
        forgotPasswordButton.addActionListener(e -> showForgotPasswordDialog());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String roleStr = (String) roleComboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convert role string to enum
        User.UserRole role = User.UserRole.valueOf(roleStr.toUpperCase());

        // Attempt login
        User user = authService.login(username, password, role);

        if (user != null) {
            // Login successful
            this.dispose(); // Close login window
            SwingUtilities.invokeLater(() -> {
                DashboardFrame dashboard = new DashboardFrame(user);
                dashboard.setVisible(true);
            });
        } else {
            // Login failed
            JOptionPane.showMessageDialog(this,
                "Invalid username, password, or role",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationFrame() {
        this.dispose(); // Close login window
        SwingUtilities.invokeLater(() -> {
            RegistrationFrame registrationFrame = new RegistrationFrame();
            registrationFrame.setVisible(true);
        });
    }

    private void showForgotPasswordDialog() {
        JDialog forgotPasswordDialog = new JDialog(this, "Reset Password", true);
        forgotPasswordDialog.setSize(400, 300);
        forgotPasswordDialog.setLocationRelativeTo(this);
        forgotPasswordDialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Reset Your Password", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField usernameResetField = new JTextField(20);
        usernameResetField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        formPanel.add(usernameResetField, gbc);
        
        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailResetField = new JTextField(20);
        emailResetField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your registered email");
        formPanel.add(emailResetField, gbc);
        
        // Role selection for verification
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> roleResetComboBox = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        formPanel.add(roleResetComboBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> forgotPasswordDialog.dispose());
        
        JButton resetButton = new JButton("Reset Password");
        resetButton.setBackground(new Color(0, 120, 212));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        
        resetButton.addActionListener(e -> {
            String username = usernameResetField.getText();
            String email = emailResetField.getText();
            String roleStr = (String) roleResetComboBox.getSelectedItem();
            
            if (username.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(forgotPasswordDialog,
                    "Please fill in all fields",
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(forgotPasswordDialog,
                    "Please enter a valid email address",
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // In a real application, this would validate the user credentials
            // and either send an email with reset instructions or allow immediate password reset
            
            // For this demo, we'll show a message indicating next steps
            JOptionPane.showMessageDialog(forgotPasswordDialog,
                "Password reset instructions have been sent to your email address.\n" +
                "Please check your inbox and follow the instructions to reset your password.",
                "Reset Instructions Sent",
                JOptionPane.INFORMATION_MESSAGE);
            
            forgotPasswordDialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(resetButton);
        
        // Add components to main panel
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        forgotPasswordDialog.add(mainPanel);
        forgotPasswordDialog.setVisible(true);
    }
} 