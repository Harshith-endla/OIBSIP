
package com.reservation.ui;

import com.reservation.model.User;
import com.reservation.service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;

public class RegisterFrame extends JFrame {

    private final AuthenticationService authenticationService;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterFrame() {
        authenticationService = new AuthenticationService();
        initializeUI();
    }

    // Configures the registration window.
    private void initializeUI() {

        setTitle("Online Reservation System - Registration");
        setSize(480, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(
                "CREATE YOUR ACCOUNT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 21));

        JLabel subtitleLabel = new JLabel(
                "Register to use the reservation system",
                SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel headingPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headingPanel.setBackground(Color.WHITE);
        headingPanel.add(titleLabel);
        headingPanel.add(subtitleLabel);

        mainPanel.add(headingPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        addField(formPanel, "Full Name", nameField);
        addField(formPanel, "Email Address", emailField);
        addField(formPanel, "Phone Number", phoneField);
        addField(formPanel, "Password", passwordField);
        addField(formPanel, "Confirm Password", confirmPasswordField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));

        registerButton.setBackground(new Color(37, 99, 235));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        registerButton.addActionListener(e -> handleRegistration());

        backButton.addActionListener(e -> returnToLogin());

        getRootPane().setDefaultButton(registerButton);
    }

    // Adds a label and input field to the registration form.
    private void addField(
            JPanel panel,
            String labelText,
            JComponent inputField) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));

        inputField.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(label);
        panel.add(inputField);
    }

    // Handles user registration.
    private void handleRegistration() {

        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        try {

            if (fullName.isEmpty()
                    || email.isEmpty()
                    || phone.isEmpty()
                    || password.length == 0
                    || confirmPassword.length == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill in all fields.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);

                return;
            }

            if (!Arrays.equals(password, confirmPassword)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Passwords do not match.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);

                return;
            }

            User registeredUser = authenticationService.registerUser(
                    fullName,
                    email,
                    phone,
                    password);

            JOptionPane.showMessageDialog(
                    this,
                    "Registration successful!\n"
                            + "Welcome, " + registeredUser.getFullName()
                            + "\nYou can now log in.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            returnToLogin();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException e) {

            if (isDuplicateEmail(e)) {

                JOptionPane.showMessageDialog(
                        this,
                        "This email address is already registered.",
                        "Registration Failed",
                        JOptionPane.WARNING_MESSAGE);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to complete registration.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);

                e.printStackTrace();
            }

        } catch (GeneralSecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to secure the password.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();

        } finally {

            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');

            passwordField.setText("");
            confirmPasswordField.setText("");
        }
    }

    // Checks whether a database error is caused by a duplicate email.
    private boolean isDuplicateEmail(SQLException exception) {

        String message = exception.getMessage();

        return message != null
                && message.contains("UNIQUE constraint failed: Users.email");
    }

    // Returns the user to the login screen.
    private void returnToLogin() {

        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);

        dispose();
    }
}
