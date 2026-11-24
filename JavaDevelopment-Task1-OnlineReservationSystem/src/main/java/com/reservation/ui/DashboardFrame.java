
package com.reservation.ui;

import com.reservation.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final User user;

    public DashboardFrame(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null.");
        }

        this.user = user;

        initializeUI();
    }

    // Configures the dashboard window.
    private void initializeUI() {

        setTitle("Online Reservation System - Dashboard");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(
                "ONLINE RESERVATION SYSTEM",
                SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + user.getFullName() + "!",
                SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 17));

        JPanel headingPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        headingPanel.setBackground(Color.WHITE);
        headingPanel.add(titleLabel);
        headingPanel.add(welcomeLabel);

        mainPanel.add(headingPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        buttonPanel.setBackground(Color.WHITE);

        JButton bookTicketButton = new JButton("Book Ticket");
        JButton trainDetailsButton = new JButton("View Train Details");
        JButton cancelReservationButton = new JButton("Cancel Reservation");
        JButton logoutButton = new JButton("Logout");

        JButton[] buttons = {
                bookTicketButton,
                trainDetailsButton,
                cancelReservationButton,
                logoutButton
        };

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 15));
            button.setFocusPainted(false);
            buttonPanel.add(button);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Temporary messages until these features are implemented.
        bookTicketButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Ticket booking feature is coming soon.",
                "Book Ticket",
                JOptionPane.INFORMATION_MESSAGE));

        trainDetailsButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Train details feature is coming soon.",
                "Train Details",
                JOptionPane.INFORMATION_MESSAGE));

        cancelReservationButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Reservation cancellation feature is coming soon.",
                "Cancel Reservation",
                JOptionPane.INFORMATION_MESSAGE));

        logoutButton.addActionListener(e -> logout());
    }

    // Returns the user to the login screen.
    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

            dispose();
        }
    }
}
