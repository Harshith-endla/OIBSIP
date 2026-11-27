package com.reservation.ui;

import com.reservation.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    private final User user;

    public AdminDashboardFrame(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null.");
        }

        if (!"ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException(
                    "Only administrators can access this dashboard.");
        }

        this.user = user;

        initializeUI();
    }

    // Configures the administrator dashboard.
    private void initializeUI() {

        setTitle("Online Reservation System - Admin Dashboard");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(
                "ADMIN DASHBOARD",
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

        JButton manageTrainsButton = new JButton("Manage Trains");
        JButton manageSchedulesButton = new JButton("Manage Train Schedules");
        JButton viewReservationsButton = new JButton("View Reservations");
        JButton logoutButton = new JButton("Logout");

        JButton[] buttons = {
                manageTrainsButton,
                manageSchedulesButton,
                viewReservationsButton,
                logoutButton
        };

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 15));
            button.setFocusPainted(false);
            buttonPanel.add(button);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Temporary messages until admin features are implemented.
        manageTrainsButton.addActionListener(e -> {

            ManageTrainsFrame manageTrainsFrame = new ManageTrainsFrame(user);

            manageTrainsFrame.setVisible(true);

            dispose();
        });

        manageSchedulesButton.addActionListener(e -> {

            ManageTrainSchedulesFrame manageSchedulesFrame = new ManageTrainSchedulesFrame(user);

            manageSchedulesFrame.setVisible(true);

            dispose();
        });

        viewReservationsButton.addActionListener(e -> {

            ViewReservationsFrame reservationsFrame = new ViewReservationsFrame(user);

            reservationsFrame.setVisible(true);

            dispose();
        });

        logoutButton.addActionListener(e -> logout());
    }

    // Returns the administrator to the login screen.
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
