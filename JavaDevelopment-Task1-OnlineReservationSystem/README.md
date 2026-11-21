Task 1: Online Reservation System
Objective
Build a GUI-based train/transport reservation system where users can log in, book tickets, and cancel bookings using a PNR number.

Technology Stack
Java (Swing or JavaFX for GUI)
JDBC
MySQL or SQLite
Feature Checklist
 Login Form: Username and password fields; deny access for invalid credentials.
 Reservation Form: Fields for passenger name, train number, train name (auto-populated from train number), class type, date of journey, source station, and destination station.
 Insert/Book Button: Save the reservation to the database and generate a unique, auto-generated PNR number.
 Booking Confirmation: Display a confirmation dialog showing booking details after successful reservation.
 Cancellation Form: PNR number input field and Fetch button to retrieve and display complete booking details.
 Confirm Cancellation: Display an "Are you sure?" dialog and remove the booking from the database after confirmation.
 Input Validation: Validate required fields, date format, and numeric train number.
Self-Sourcing Guidelines
Search YouTube for "Java Swing JDBC login form tutorial" to understand GUI and database connection patterns.
Search "SQLite Java JDBC Maven setup" for database setup instructions. SQLite does not require a separate database server.
Refer to the official Java documentation for:
JFrame
JTextField
JComboBox
PreparedStatement (for SQL injection prevention)
