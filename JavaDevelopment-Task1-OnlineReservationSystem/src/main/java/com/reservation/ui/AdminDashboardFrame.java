
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

        manageTrainsButton.addActionListener(e -> {

            try {

                ManageTrainsFrame manageTrainsFrame = new ManageTrainsFrame(user);

                manageTrainsFrame.setVisible(true);

                dispose();

            } catch (IllegalArgumentException exception) {

                showAccessDeniedDialog();
            }
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

    // Displays a custom access-restricted popup.
    private void showAccessDeniedDialog() {

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(
                dialogPanel,
                BoxLayout.Y_AXIS));

        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(new EmptyBorder(25, 30, 20, 30));

        // Security icon.
        JLabel iconLabel = new JLabel("🔒");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        iconPanel.setBackground(new Color(252, 232, 232));
        iconPanel.setPreferredSize(new Dimension(48, 48));
        iconPanel.setMaximumSize(new Dimension(48, 48));
        iconPanel.add(iconLabel);

        // Dialog heading.
        JLabel headingLabel = new JLabel("Access Restricted");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 21));
        headingLabel.setForeground(new Color(40, 45, 58));

        JLabel applicationLabel = new JLabel(
                "Online Reservation System");
        applicationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        applicationLabel.setForeground(new Color(115, 120, 130));

        // Explanation.
        JLabel messageLabel = new JLabel(
                "<html><div style='width:340px'>"
                        + "You cannot access Train Management because "
                        + "your account does not have the required "
                        + "administrator permissions."
                        + "</div></html>");

        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(85, 93, 107));

        // Buttons.
        JButton goBackButton = new JButton("Go Back");
        JButton loginButton = new JButton("Return to Login");

        styleDialogButton(
                goBackButton,
                new Color(233, 236, 243),
                new Color(48, 55, 71));

        styleDialogButton(
                loginButton,
                new Color(53, 79, 133),
                Color.WHITE);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));

        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(goBackButton);
        buttonPanel.add(loginButton);

        // Arrange dialog content.
        dialogPanel.add(iconPanel);
        dialogPanel.add(Box.createVerticalStrut(18));
        dialogPanel.add(headingLabel);
        dialogPanel.add(Box.createVerticalStrut(4));
        dialogPanel.add(applicationLabel);
        dialogPanel.add(Box.createVerticalStrut(20));
        dialogPanel.add(messageLabel);
        dialogPanel.add(Box.createVerticalStrut(22));
        dialogPanel.add(buttonPanel);

        dialogPanel.setPreferredSize(new Dimension(420, 240));

        JOptionPane optionPane = new JOptionPane(
                dialogPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION);

        JDialog dialog = optionPane.createDialog(
                this,
                "Access Restricted");

        // Close the popup and remain on the dashboard.
        goBackButton.addActionListener(e -> dialog.dispose());

        // Return the user to the login screen.
        loginButton.addActionListener(e -> {

            dialog.dispose();

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

            dispose();
        });

        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Applies consistent styling to popup buttons.
    private void styleDialogButton(
            JButton button,
            Color background,
            Color foreground) {

        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
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
