package com.reservation.ui;

import com.reservation.model.User;
import com.reservation.service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class LoginFrame extends JFrame {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final AuthenticationService authenticationService;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame() {

        authenticationService = new AuthenticationService();

        initializeUI();
    }

    // Configures the login window.
    private void initializeUI() {

        setTitle("Online Reservation System - Login");
        setSize(450, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(
                "ONLINE RESERVATION SYSTEM",
                SwingConstants.CENTER);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel subtitleLabel = new JLabel(
                "Login to your account",
                SwingConstants.CENTER);

        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel headingPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headingPanel.setBackground(Color.WHITE);
        headingPanel.add(titleLabel);
        headingPanel.add(subtitleLabel);

        mainPanel.add(headingPanel, BorderLayout.NORTH);

        // Login form fields.
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 8, 8));
        formPanel.setBackground(Color.WHITE);

        JLabel emailLabel = new JLabel("Email Address");
        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(messageLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Login and registration buttons.
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));

        loginButton.setBackground(new Color(37, 99, 235));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Button actions.
        loginButton.addActionListener(e -> handleLogin());

        registerButton.addActionListener(e -> openRegistration());

        getRootPane().setDefaultButton(loginButton);
    }

    // Opens the registration window.
    private void openRegistration() {

        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setVisible(true);

        dispose();
    }

    // Validates the email address format.
    private boolean isValidEmail(String email) {

        return email != null
                && EMAIL_PATTERN.matcher(email).matches();
    }

    // Handles user authentication.
    private void handleLogin() {

        String email = emailField.getText().trim();

        if (!isValidEmail(email)) {

            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter a valid email address.");

            passwordField.setText("");

            return;
        }

        char[] password = passwordField.getPassword();

        try {

            User user = authenticationService.login(email, password);

            if (user != null) {

                messageLabel.setForeground(new Color(22, 163, 74));
                messageLabel.setText("Login successful!");

                JOptionPane.showMessageDialog(
                        this,
                        "Welcome, " + user.getFullName() + "!",
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                if ("ADMIN".equals(user.getRole())) {

                    AdminDashboardFrame adminDashboardFrame = new AdminDashboardFrame(user);

                    adminDashboardFrame.setVisible(true);

                } else {

                    DashboardFrame dashboardFrame = new DashboardFrame(user);

                    dashboardFrame.setVisible(true);
                }

                dispose();

            } else {

                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid email or password.");

            }

        } catch (SQLException | GeneralSecurityException e) {

            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Unable to complete login.");

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while logging in.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);

        } finally {

            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }
}