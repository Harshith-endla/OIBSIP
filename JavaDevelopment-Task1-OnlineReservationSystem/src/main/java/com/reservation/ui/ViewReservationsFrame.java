
package com.reservation.ui;

import com.reservation.model.Reservation;
import com.reservation.model.User;
import com.reservation.service.ReservationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ViewReservationsFrame extends JFrame {

    private final User user;
    private final ReservationService reservationService;

    private JTable reservationTable;
    private DefaultTableModel tableModel;

    public ViewReservationsFrame(User user) {

        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException(
                    "Only administrators can view reservations.");
        }

        this.user = user;
        this.reservationService = new ReservationService();

        initializeUI();
        loadReservations();
    }

    // Configures the reservation viewing window.
    private void initializeUI() {

        setTitle("Online Reservation System - View Reservations");
        setSize(1150, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(
                "ALL RESERVATIONS",
                SwingConstants.CENTER);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Reservation table.
        String[] columns = {
                "Reservation ID",
                "PNR",
                "Passenger Names",
                "Train Number",
                "Train Name",
                "Source",
                "Destination",
                "Journey Date",
                "Departure",
                "Arrival",
                "Booking Date",
                "Passengers",
                "Total Fare",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationTable = new JTable(tableModel);

        reservationTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        reservationTable.setRowHeight(25);

        reservationTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(reservationTable);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Configure column widths.
        int[] columnWidths = {
                100, 120, 200, 110, 180, 130, 130,
                110, 90, 90, 110, 90, 100, 100
        };

        for (int i = 0; i < columnWidths.length; i++) {
            reservationTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(columnWidths[i]);
        }

        // Action buttons.
        JPanel buttonPanel = new JPanel(
                new GridLayout(1, 2, 10, 10));

        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Dashboard");

        JButton[] buttons = {
                refreshButton,
                backButton
        };

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 13));
            button.setFocusPainted(false);
            buttonPanel.add(button);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Button actions.
        refreshButton.addActionListener(
                e -> loadReservations());

        backButton.addActionListener(
                e -> goBack());
    }

    // Loads all reservations into the table.
    private void loadReservations() {

        try {

            List<Reservation> reservations = reservationService.getAllReservations();

            tableModel.setRowCount(0);

            for (Reservation reservation : reservations) {

                tableModel.addRow(new Object[] {

                        reservation.getReservationId(),
                        reservation.getPnr(),
                        reservation.getPassengerName(),
                        reservation.getTrainNumber(),
                        reservation.getTrainName(),
                        reservation.getSource(),
                        reservation.getDestination(),
                        reservation.getJourneyDate(),
                        reservation.getDepartureTime(),
                        reservation.getArrivalTime(),
                        reservation.getBookingDate(),
                        reservation.getPassengerCount(),
                        reservation.getTotalFare(),
                        reservation.getStatus()
                });
            }

            if (reservations.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No reservations are available.",
                        "Reservations",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {

            showError(
                    "Unable to load reservation records.",
                    e);
        }
    }

    // Returns to the administrator dashboard.
    private void goBack() {

        AdminDashboardFrame dashboard = new AdminDashboardFrame(user);

        dashboard.setVisible(true);

        dispose();
    }

    // Displays database errors.
    private void showError(String message, SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                message + "\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
