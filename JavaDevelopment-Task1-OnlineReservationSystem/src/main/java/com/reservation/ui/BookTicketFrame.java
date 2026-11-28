
package com.reservation.ui;

import com.reservation.model.Reservation;
import com.reservation.model.ReservationPassenger;
import com.reservation.model.TrainSchedule;
import com.reservation.model.User;
import com.reservation.service.ReservationService;
import com.reservation.service.TrainScheduleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookTicketFrame extends JFrame {

    private final User user;

    private final ReservationService reservationService;
    private final TrainScheduleService scheduleService;

    private JComboBox<TrainSchedule> scheduleComboBox;
    private JSpinner passengerCountSpinner;

    private JPanel passengerPanel;

    private JLabel trainDetailsLabel;
    private JLabel fareLabel;

    private final List<JTextField> passengerNameFields = new ArrayList<>();

    private final List<JTextField> dateOfBirthFields = new ArrayList<>();

    private final List<JComboBox<String>> genderComboBoxes = new ArrayList<>();

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("en-IN"));

    public BookTicketFrame(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "Logged-in user is required.");
        }

        this.user = user;

        this.reservationService = new ReservationService();
        this.scheduleService = new TrainScheduleService();

        setTitle("Online Reservation System - Book Ticket");
        setSize(950, 720);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeComponents();
        loadAvailableSchedules();
    }

    // Initialize the booking screen.
    private void initializeComponents() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel headingLabel = new JLabel("Book Train Ticket");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.add(headingLabel, BorderLayout.WEST);

        mainPanel.add(headingPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(
                new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(createSchedulePanel());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createPassengerSelectionPanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createPassengerPanel());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Create the train schedule selection section.
    private JPanel createSchedulePanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                "Select Train Schedule"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        panel.add(new JLabel("Available Trains:"), gbc);

        scheduleComboBox = new JComboBox<>();

        scheduleComboBox.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {

                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus);

                        if (value instanceof TrainSchedule schedule) {
                            setText(
                                    schedule.getTrainNumber()
                                            + " - "
                                            + schedule.getTrainName()
                                            + " | "
                                            + schedule.getJourneyDate()
                                            + " | "
                                            + schedule.getDepartureTime());
                        }

                        return this;
                    }
                });

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;

        panel.add(scheduleComboBox, gbc);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(
                event -> loadAvailableSchedules());

        gbc.gridx = 2;
        gbc.weightx = 0;

        panel.add(refreshButton, gbc);

        trainDetailsLabel = new JLabel(
                "Select a train to view journey details.");

        trainDetailsLabel.setFont(
                new Font("Arial", Font.PLAIN, 13));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;

        panel.add(trainDetailsLabel, gbc);

        scheduleComboBox.addActionListener(
                event -> updateScheduleDetails());

        return panel;
    }

    // Create the passenger count section.
    private JPanel createPassengerSelectionPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.setBorder(BorderFactory.createTitledBorder(
                "Passenger Information"));

        panel.add(new JLabel("Number of Passengers:"));

        passengerCountSpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, 7, 1));

        // Configure the spinner editor.
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(passengerCountSpinner, "0");

        passengerCountSpinner.setEditor(editor);

        JFormattedTextField textField = editor.getTextField();

        NumberFormatter formatter = (NumberFormatter) textField.getFormatter();

        // Allow manual input so custom validation can handle invalid values.
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);

        // Prevent automatic commit or revert before custom validation.
        textField.setFocusLostBehavior(
                JFormattedTextField.PERSIST);

        // Override the default Enter action.
        textField.getInputMap().put(
                KeyStroke.getKeyStroke("ENTER"),
                "validatePassengerCount");

        textField.getActionMap().put(
                "validatePassengerCount",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent event) {
                        validatePassengerCountInput();
                    }
                });

        // Validate when the user moves away from the field.
        textField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent event) {
                validatePassengerCountInput();
            }
        });

        // Update passenger fields and fare whenever the count changes.
        passengerCountSpinner.addChangeListener(event -> {
            updatePassengerFields();
            updateFare();
        });

        panel.add(passengerCountSpinner);

        return panel;
    }

    // Validate manually entered passenger count.
    private void validatePassengerCountInput() {

        JFormattedTextField textField = ((JSpinner.DefaultEditor) passengerCountSpinner.getEditor())
                .getTextField();

        String enteredValue = textField.getText().trim();

        try {

            int passengerCount = Integer.parseInt(enteredValue);

            if (passengerCount < 1 || passengerCount > 7) {

                JOptionPane.showMessageDialog(
                        this,
                        "Passenger count must be between 1 and 7.",
                        "Invalid Passenger Count",
                        JOptionPane.WARNING_MESSAGE);

                // Restore the previous valid value.
                textField.setText(
                        String.valueOf(passengerCountSpinner.getValue()));

                return;
            }

            // Accept the valid passenger count.
            passengerCountSpinner.setValue(passengerCount);

            textField.setText(String.valueOf(passengerCount));

        } catch (NumberFormatException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid passenger count.",
                    "Invalid Passenger Count",
                    JOptionPane.WARNING_MESSAGE);

            // Restore the previous valid value.
            textField.setText(
                    String.valueOf(passengerCountSpinner.getValue()));
        }
    }

    // Create the dynamic passenger input section.
    private JPanel createPassengerPanel() {

        passengerPanel = new JPanel(new GridBagLayout());

        passengerPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Passenger Details"));

        updatePassengerFields();

        return passengerPanel;
    }

    // Generate passenger input rows based on selected count.
    private void updatePassengerFields() {

        passengerNameFields.clear();
        dateOfBirthFields.clear();
        genderComboBoxes.clear();

        passengerPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        String[] headers = {
                "Passenger Name",
                "Date of Birth (yyyy-MM-dd)",
                "Gender"
        };

        for (int column = 0; column < headers.length; column++) {

            gbc.gridx = column;
            gbc.gridy = 0;

            JLabel label = new JLabel(headers[column]);
            label.setFont(new Font("Arial", Font.BOLD, 12));

            passengerPanel.add(label, gbc);
        }

        int passengerCount = (Integer) passengerCountSpinner.getValue();

        for (int i = 0; i < passengerCount; i++) {

            JTextField nameField = new JTextField(15);
            JTextField dobField = new JTextField(12);

            JComboBox<String> genderComboBox = new JComboBox<>(
                    new String[] {
                            "Select Gender",
                            "MALE",
                            "FEMALE",
                            "OTHER"
                    });

            passengerNameFields.add(nameField);
            dateOfBirthFields.add(dobField);
            genderComboBoxes.add(genderComboBox);

            gbc.gridy = i + 1;

            gbc.gridx = 0;
            passengerPanel.add(nameField, gbc);

            gbc.gridx = 1;
            passengerPanel.add(dobField, gbc);

            gbc.gridx = 2;
            passengerPanel.add(genderComboBox, gbc);
        }

        passengerPanel.revalidate();
        passengerPanel.repaint();
    }

    // Display the selected schedule details.
    private void updateScheduleDetails() {

        TrainSchedule schedule = (TrainSchedule) scheduleComboBox.getSelectedItem();

        if (schedule == null) {
            trainDetailsLabel.setText(
                    "No train schedule selected.");
            updateFare();
            return;
        }

        trainDetailsLabel.setText(
                "<html>"
                        + "Journey Date: " + schedule.getJourneyDate()
                        + " &nbsp; | &nbsp; "
                        + "Departure: " + schedule.getDepartureTime()
                        + " &nbsp; | &nbsp; "
                        + "Arrival: " + schedule.getArrivalTime()
                        + " &nbsp; | &nbsp; "
                        + "Available Seats: " + schedule.getAvailableSeats()
                        + " &nbsp; | &nbsp; "
                        + "Fare per Seat: "
                        + currencyFormat.format(schedule.getFarePerSeat())
                        + "</html>");

        updateFare();
    }

    // Calculate and display the total fare.
    private void updateFare() {

        TrainSchedule schedule = (TrainSchedule) scheduleComboBox.getSelectedItem();

        if (schedule == null) {
            fareLabel.setText("Total Fare: ₹0.00");
            return;
        }

        int passengerCount = (Integer) passengerCountSpinner.getValue();

        long totalFare = (long) schedule.getFarePerSeat() * passengerCount;

        fareLabel.setText(
                "Total Fare: "
                        + currencyFormat.format(totalFare));
    }

    // Create the bottom action buttons.
    private JPanel createButtonPanel() {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT));

        fareLabel = new JLabel("Total Fare: ₹0.00");
        fareLabel.setFont(
                new Font("Arial", Font.BOLD, 16));

        JButton bookButton = new JButton("Confirm Booking");
        JButton backButton = new JButton("Close");

        bookButton.addActionListener(
                event -> confirmBooking());

        backButton.addActionListener(
                event -> dispose());

        panel.add(fareLabel);
        panel.add(bookButton);
        panel.add(backButton);

        return panel;
    }

    // Load available train schedules.
    private void loadAvailableSchedules() {

        try {

            List<TrainSchedule> schedules = scheduleService.getAvailableSchedulesForBooking();

            scheduleComboBox.removeAllItems();

            for (TrainSchedule schedule : schedules) {
                scheduleComboBox.addItem(schedule);
            }

            if (schedules.isEmpty()) {

                trainDetailsLabel.setText(
                        "No future train schedules with available seats.");

                fareLabel.setText("Total Fare: ₹0.00");

            } else {

                updateScheduleDetails();
            }

        } catch (SQLException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load train schedules.\n"
                            + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Validate and collect passenger details.
    private List<ReservationPassenger> collectPassengers() {

        List<ReservationPassenger> passengers = new ArrayList<>();

        for (int i = 0; i < passengerNameFields.size(); i++) {

            String name = passengerNameFields.get(i).getText().trim();

            String dateOfBirth = dateOfBirthFields.get(i).getText().trim();

            String gender = (String) genderComboBoxes.get(i).getSelectedItem();

            if (name.isEmpty()
                    || dateOfBirth.isEmpty()
                    || gender == null
                    || gender.equals("Select Gender")) {

                throw new IllegalArgumentException(
                        "Please complete all details for passenger "
                                + (i + 1) + ".");
            }

            passengers.add(
                    new ReservationPassenger(
                            0,
                            0,
                            0,
                            name,
                            dateOfBirth,
                            gender,
                            null));
        }

        return passengers;
    }

    // Confirm and submit the booking.
    private void confirmBooking() {

        TrainSchedule schedule = (TrainSchedule) scheduleComboBox.getSelectedItem();

        if (schedule == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an available train schedule.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        try {

            List<ReservationPassenger> passengers = collectPassengers();

            long totalFare = (long) schedule.getFarePerSeat()
                    * passengers.size();

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Train: " + schedule.getTrainName()
                            + "\nJourney Date: " + schedule.getJourneyDate()
                            + "\nPassengers: " + passengers.size()
                            + "\nTotal Fare: "
                            + currencyFormat.format(totalFare)
                            + "\n\nConfirm your booking?",
                    "Confirm Reservation",
                    JOptionPane.YES_NO_OPTION);

            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            Reservation reservation = reservationService.createReservation(
                    user,
                    schedule.getScheduleId(),
                    passengers);

            JOptionPane.showMessageDialog(
                    this,
                    "Ticket booked successfully!"
                            + "\n\nPNR: " + reservation.getPnr()
                            + "\nTrain: " + reservation.getTrainName()
                            + "\nJourney Date: "
                            + reservation.getJourneyDate()
                            + "\nPassengers: "
                            + reservation.getPassengerCount()
                            + "\nTotal Fare: "
                            + currencyFormat.format(
                                    reservation.getTotalFare()),
                    "Booking Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            loadAvailableSchedules();

            passengerCountSpinner.setValue(1);
            updatePassengerFields();

        } catch (IllegalArgumentException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Invalid Booking Details",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to complete the booking.\n"
                            + exception.getMessage(),
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
