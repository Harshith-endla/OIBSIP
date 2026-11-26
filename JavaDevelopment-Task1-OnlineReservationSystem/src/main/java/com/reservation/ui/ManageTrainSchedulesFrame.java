
package com.reservation.ui;

import com.reservation.model.Train;
import com.reservation.model.TrainSchedule;
import com.reservation.model.User;
import com.reservation.service.TrainScheduleService;
import com.reservation.service.TrainService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ManageTrainSchedulesFrame extends JFrame {

    private final User user;
    private final TrainScheduleService scheduleService;
    private final TrainService trainService;

    private JTable scheduleTable;
    private DefaultTableModel tableModel;

    public ManageTrainSchedulesFrame(User user) {

        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException(
                    "Only administrators can manage train schedules.");
        }

        this.user = user;
        this.scheduleService = new TrainScheduleService();
        this.trainService = new TrainService();

        initializeUI();
        loadSchedules();
    }

    // Configures the train schedule management window.
    private void initializeUI() {

        setTitle("Online Reservation System - Manage Train Schedules");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(
                "MANAGE TRAIN SCHEDULES",
                SwingConstants.CENTER);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Train schedule table.
        String[] columns = {
                "Schedule ID",
                "Train Number",
                "Train Name",
                "Journey Date",
                "Departure",
                "Arrival",
                "Total Seats",
                "Available Seats",
                "Fare"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);

        scheduleTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        scheduleTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(scheduleTable);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons.
        JPanel buttonPanel = new JPanel(
                new GridLayout(2, 3, 10, 10));

        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Schedule");
        JButton updateButton = new JButton("Update Schedule");
        JButton deleteButton = new JButton("Delete Schedule");
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
        addButton.addActionListener(e -> addSchedule());
        updateButton.addActionListener(e -> updateSchedule());
        deleteButton.addActionListener(e -> deleteSchedule());
        refreshButton.addActionListener(e -> loadSchedules());
        backButton.addActionListener(e -> goBack());
    }

    // Loads all train schedules into the table.
    private void loadSchedules() {

        try {

            List<TrainSchedule> schedules = scheduleService.getAllSchedules();

            tableModel.setRowCount(0);

            for (TrainSchedule schedule : schedules) {

                tableModel.addRow(new Object[] {
                        schedule.getScheduleId(),
                        schedule.getTrainNumber(),
                        schedule.getTrainName(),
                        schedule.getJourneyDate(),
                        schedule.getDepartureTime(),
                        schedule.getArrivalTime(),
                        schedule.getTotalSeats(),
                        schedule.getAvailableSeats(),
                        schedule.getFarePerSeat()
                });
            }

        } catch (SQLException e) {

            showError("Unable to load train schedules.", e);
        }
    }

    // Opens a form to add a new train schedule.
    private void addSchedule() {

        JComboBox<Train> trainComboBox = createTrainComboBox();

        if (trainComboBox == null) {
            return;
        }

        JTextField dateField = new JTextField();
        JTextField departureField = new JTextField();
        JTextField arrivalField = new JTextField();
        JTextField seatsField = new JTextField();
        JTextField fareField = new JTextField();

        JPanel formPanel = createScheduleForm(
                trainComboBox,
                dateField,
                departureField,
                arrivalField,
                seatsField,
                fareField);

        int choice = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Add Train Schedule",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            Train selectedTrain = (Train) trainComboBox.getSelectedItem();

            if (selectedTrain == null) {
                throw new IllegalArgumentException(
                        "Please select a train.");
            }

            boolean added = scheduleService.addSchedule(
                    selectedTrain.getTrainId(),
                    dateField.getText(),
                    departureField.getText(),
                    arrivalField.getText(),
                    Integer.parseInt(seatsField.getText().trim()),
                    Integer.parseInt(fareField.getText().trim()));

            if (added) {

                JOptionPane.showMessageDialog(
                        this,
                        "Train schedule added successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadSchedules();
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Total seats and fare must be valid whole numbers.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Invalid Schedule Details",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException e) {

            showError(
                    "Unable to add schedule. A schedule may already exist "
                            + "for this train on the selected date.",
                    e);
        }
    }

    // Opens a form to update the selected train schedule.
    private void updateSchedule() {

        int selectedRow = scheduleTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a schedule to update.",
                    "No Schedule Selected",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        int scheduleId = (int) tableModel.getValueAt(selectedRow, 0);

        try {

            TrainSchedule schedule = scheduleService.getScheduleById(scheduleId);

            if (schedule == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Schedule record was not found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE);

                loadSchedules();
                return;
            }

            JComboBox<Train> trainComboBox = createTrainComboBox();

            if (trainComboBox == null) {
                return;
            }

            selectTrain(trainComboBox, schedule.getTrainId());

            JTextField dateField = new JTextField(schedule.getJourneyDate());

            JTextField departureField = new JTextField(schedule.getDepartureTime());

            JTextField arrivalField = new JTextField(schedule.getArrivalTime());

            JTextField seatsField = new JTextField(
                    String.valueOf(schedule.getTotalSeats()));

            JTextField fareField = new JTextField(
                    String.valueOf(schedule.getFarePerSeat()));

            JPanel formPanel = createScheduleForm(
                    trainComboBox,
                    dateField,
                    departureField,
                    arrivalField,
                    seatsField,
                    fareField);

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    formPanel,
                    "Update Train Schedule",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                return;
            }

            Train selectedTrain = (Train) trainComboBox.getSelectedItem();

            if (selectedTrain == null) {
                throw new IllegalArgumentException(
                        "Please select a train.");
            }

            boolean updated = scheduleService.updateSchedule(
                    scheduleId,
                    selectedTrain.getTrainId(),
                    dateField.getText(),
                    departureField.getText(),
                    arrivalField.getText(),
                    Integer.parseInt(seatsField.getText().trim()),
                    Integer.parseInt(fareField.getText().trim()));

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Train schedule updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadSchedules();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Schedule record was not found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Total seats and fare must be valid whole numbers.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Invalid Schedule Details",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException e) {

            showError(
                    "Unable to update schedule. The selected train may "
                            + "already have a schedule on that date.",
                    e);
        }
    }

    // Deletes the selected train schedule.
    private void deleteSchedule() {

        int selectedRow = scheduleTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a schedule to delete.",
                    "No Schedule Selected",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        int scheduleId = (int) tableModel.getValueAt(selectedRow, 0);

        String trainNumber = tableModel.getValueAt(selectedRow, 1).toString();

        String journeyDate = tableModel.getValueAt(selectedRow, 3).toString();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the schedule for train "
                        + trainNumber + " on " + journeyDate + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean deleted = scheduleService.deleteSchedule(scheduleId);

            if (deleted) {

                JOptionPane.showMessageDialog(
                        this,
                        "Train schedule deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadSchedules();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Schedule record was not found.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {

            showError(
                    "Unable to delete this schedule. It may have "
                            + "associated reservations.",
                    e);
        }
    }

    // Creates a dropdown containing all available trains.
    private JComboBox<Train> createTrainComboBox() {

        JComboBox<Train> trainComboBox = new JComboBox<>();

        try {

            List<Train> trains = trainService.getAllTrains();

            if (trains.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No trains are available. Please add a train first.",
                        "No Trains Found",
                        JOptionPane.WARNING_MESSAGE);

                return null;
            }

            for (Train train : trains) {
                trainComboBox.addItem(train);
            }

            return trainComboBox;

        } catch (SQLException e) {

            showError("Unable to load train records.", e);
            return null;
        }
    }

    // Selects a train in the dropdown using its ID.
    private void selectTrain(
            JComboBox<Train> trainComboBox,
            int trainId) {

        for (int i = 0; i < trainComboBox.getItemCount(); i++) {

            Train train = trainComboBox.getItemAt(i);

            if (train.getTrainId() == trainId) {
                trainComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    // Creates the schedule input form.
    private JPanel createScheduleForm(
            JComboBox<Train> trainComboBox,
            JTextField dateField,
            JTextField departureField,
            JTextField arrivalField,
            JTextField seatsField,
            JTextField fareField) {

        JPanel formPanel = new JPanel(
                new GridLayout(0, 2, 10, 10));

        formPanel.add(new JLabel("Select Train:"));
        formPanel.add(trainComboBox);

        formPanel.add(new JLabel("Journey Date (yyyy-MM-dd):"));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Departure Time (HH:mm):"));
        formPanel.add(departureField);

        formPanel.add(new JLabel("Arrival Time (HH:mm):"));
        formPanel.add(arrivalField);

        formPanel.add(new JLabel("Total Seats:"));
        formPanel.add(seatsField);

        formPanel.add(new JLabel("Fare Per Seat:"));
        formPanel.add(fareField);

        return formPanel;
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
