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

public class RegistrationFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private final AuthenticationService authService;

    public RegistrationFrame() {
        authService = new AuthenticationService();
        
        setTitle("Attendance Tracker - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
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
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter username");
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter password");
        formPanel.add(passwordField, gbc);

        // Confirm Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm password");
        formPanel.add(confirmPasswordField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter email");
        formPanel.add(emailField, gbc);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"Teacher", "Student"});
        formPanel.add(roleComboBox, gbc);

        // Register button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 120, 212));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(registerButton, gbc);

        // Back to login link
        gbc.gridy = 6;
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel loginLabel = new JLabel("Already have an account?");
        JButton loginButton = new JButton("Login");
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setForeground(new Color(0, 120, 212));
        loginPanel.add(loginLabel);
        loginPanel.add(loginButton);
        formPanel.add(loginPanel, gbc);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Add action listeners
        registerButton.addActionListener(e -> handleRegistration());
        loginButton.addActionListener(e -> showLoginFrame());
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText();
        String roleStr = (String) roleComboBox.getSelectedItem();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create user object
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(User.UserRole.valueOf(roleStr.toUpperCase()));

        // Attempt registration
        if (authService.register(user)) {
            JOptionPane.showMessageDialog(this,
                "Registration successful! Please login.",
                "Registration Success",
                JOptionPane.INFORMATION_MESSAGE);
            showLoginFrame();
        } else {
            JOptionPane.showMessageDialog(this,
                "Registration failed. Username may already exist.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLoginFrame() {
        this.dispose(); // Close registration window
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
} 