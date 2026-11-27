
package com.reservation.ui;

import com.reservation.model.Train;
import com.reservation.model.User;
import com.reservation.service.TrainService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ManageTrainsFrame extends JFrame {

    private final User user;
    private final TrainService trainService;

    private JTable trainTable;
    private DefaultTableModel tableModel;

    public ManageTrainsFrame(User user) {

        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException(
                    "Only administrators can manage trains.");
        }

        this.user = user;
        this.trainService = new TrainService();

        initializeUI();
        loadTrains();
    }

    // Configures the train management window.
    private void initializeUI() {

        setTitle("Online Reservation System - Manage Trains");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(
                "MANAGE TRAINS",
                SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Train table.
        String[] columns = {
                "Train ID",
                "Train Number",
                "Train Name",
                "Source",
                "Destination"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        trainTable = new JTable(tableModel);
        trainTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        trainTable.setRowHeight(25);

        // Hide Train ID from the UI while retaining it in the table model.
        trainTable.removeColumn(
                trainTable.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(trainTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons.
        JPanel buttonPanel = new JPanel(
                new GridLayout(2, 3, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Train");
        JButton updateButton = new JButton("Update Train");
        JButton deleteButton = new JButton("Delete Train");
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Dashboard");

        JButton[] buttons = {
                addButton,
                updateButton,
                deleteButton,
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
        addButton.addActionListener(e -> addTrain());
        updateButton.addActionListener(e -> updateTrain());
        deleteButton.addActionListener(e -> deleteTrain());
        refreshButton.addActionListener(e -> loadTrains());
        backButton.addActionListener(e -> goBack());
    }

    // Loads all trains into the table.
    private void loadTrains() {

        try {

            List<Train> trains = trainService.getAllTrains();

            tableModel.setRowCount(0);

            for (Train train : trains) {

                tableModel.addRow(new Object[] {
                        train.getTrainId(),
                        train.getTrainNumber(),
                        train.getTrainName(),
                        train.getSource(),
                        train.getDestination()
                });
            }

        } catch (SQLException e) {

            showError("Unable to load train records.", e);
        }
    }

    // Opens a form to add a new train.
    private void addTrain() {

        JTextField numberField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField sourceField = new JTextField();
        JTextField destinationField = new JTextField();

        Object[] fields = {
                "Train Number:", numberField,
                "Train Name:", nameField,
                "Source:", sourceField,
                "Destination:", destinationField
        };

        int choice = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add New Train",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            boolean added = trainService.addTrain(
                    numberField.getText(),
                    nameField.getText(),
                    sourceField.getText(),
                    destinationField.getText());

            if (added) {

                JOptionPane.showMessageDialog(
                        this,
                        "Train added successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadTrains();
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Invalid Train Details",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException e) {

            showError(
                    "Unable to add train. Check whether the train number already exists.",
                    e);
        }
    }

    // Opens a form to update the selected train.
    private void updateTrain() {

        int selectedRow = trainTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a train to update.",
                    "No Train Selected",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        int modelRow = trainTable.convertRowIndexToModel(selectedRow);

        int trainId = (int) tableModel.getValueAt(modelRow, 0);

        JTextField numberField = new JTextField(
                tableModel.getValueAt(modelRow, 1).toString());

        JTextField nameField = new JTextField(
                tableModel.getValueAt(modelRow, 2).toString());

        JTextField sourceField = new JTextField(
                tableModel.getValueAt(modelRow, 3).toString());

        JTextField destinationField = new JTextField(
                tableModel.getValueAt(modelRow, 4).toString());

        Object[] fields = {
                "Train Number:", numberField,
                "Train Name:", nameField,
                "Source:", sourceField,
                "Destination:", destinationField
        };

        int choice = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Update Train",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            boolean updated = trainService.updateTrain(
                    trainId,
                    numberField.getText(),
                    nameField.getText(),
                    sourceField.getText(),
                    destinationField.getText());

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Train updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadTrains();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Train record was not found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Invalid Train Details",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException e) {

            showError(
                    "Unable to update train. Check whether the train number already exists.",
                    e);
        }
    }

    // Deletes the selected train.
    private void deleteTrain() {

        int selectedRow = trainTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a train to delete.",
                    "No Train Selected",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        int modelRow = trainTable.convertRowIndexToModel(selectedRow);

        int trainId = (int) tableModel.getValueAt(modelRow, 0);

        String trainName = tableModel.getValueAt(modelRow, 2).toString();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + trainName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean deleted = trainService.deleteTrain(trainId);

            if (deleted) {

                JOptionPane.showMessageDialog(
                        this,
                        "Train deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadTrains();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Train record was not found.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (IllegalStateException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Train Cannot Be Deleted",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException e) {

            showError(
                    "Unable to delete this train because of a database error.",
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
