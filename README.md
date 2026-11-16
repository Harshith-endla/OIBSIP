# Java Development – Task 3: ATM Interface

**Track:** Java Development  
**Completion Rule:** Complete at least 2 of the 5 assigned tasks.

This repository contains the Java Development internship task requirements, including Task 1 (Online Reservation System) and Task 3 (ATM Interface).

---

## Task 1: Online Reservation System

### Objective

Build a GUI-based train/transport reservation system where users can log in, book tickets, and cancel bookings using a PNR number.

### Technology Stack

- Java (Swing or JavaFX for GUI)
- JDBC
- MySQL or SQLite

### Feature Checklist

- [ ] **Login Form:** Username and password fields; deny access for invalid credentials.
- [ ] **Reservation Form:** Fields for passenger name, train number, train name (auto-populated from train number), class type, date of journey, source station, and destination station.
- [ ] **Insert/Book Button:** Save the reservation to the database and generate a unique, auto-generated PNR number.
- [ ] **Booking Confirmation:** Display a confirmation dialog showing booking details after successful reservation.
- [ ] **Cancellation Form:** PNR number input field and Fetch button to retrieve and display complete booking details.
- [ ] **Confirm Cancellation:** Display an "Are you sure?" dialog and remove the booking from the database after confirmation.
- [ ] **Input Validation:** Validate required fields, date format, and numeric train number.

### Self-Sourcing Guidelines

- Search YouTube for **"Java Swing JDBC login form tutorial"** to understand GUI and database connection patterns.
- Search **"SQLite Java JDBC Maven setup"** for database setup instructions. SQLite does not require a separate database server.
- Refer to the official Java documentation for:
  - `JFrame`
  - `JTextField`
  - `JComboBox`
  - `PreparedStatement` (for SQL injection prevention)

---

## Task 3: ATM Interface

### Objective

Build a console-based simulation of an ATM machine that allows users to authenticate with a PIN and perform standard banking transactions.

### Technology Stack

- Java
- Console Application
- Object-Oriented Programming (OOP)
- Multiple Java classes

### Feature Checklist

- [ ] **User Authentication:** Startup prompt for User ID and PIN; deny access after 3 incorrect attempts.
- [ ] **Main Menu:** Display the following options after successful login:
  1. Transaction History
  2. Withdraw
  3. Deposit
  4. Transfer
  5. Quit

- [ ] **Transaction History:** Display a log of all past transactions in the current session.
- [ ] **Withdraw:** Prompt for an amount, validate sufficient balance, update the balance, and log the transaction.
- [ ] **Deposit:** Prompt for an amount, update the balance, and log the transaction.
- [ ] **Transfer:** Prompt for recipient account ID and amount, validate available balance, update both accounts, and log the transaction.
- [ ] **Quit:** Display a goodbye message and exit the application.
- [ ] **Balance Validation:** Check the account balance before any withdrawal or transfer. Display "Insufficient Funds" if the balance is too low.
- [ ] **Transaction Storage:** Store all transactions in an `ArrayList` and display them clearly in Transaction History.
- [ ] **Object-Oriented Design:** Implement at least 5 distinct Java classes:
  - `ATM`
  - `Account`
  - `Transaction`
  - `Bank`
  - `Main`

### Self-Sourcing Guidelines

- Search YouTube for **"Java ATM machine project OOP tutorial"**.
- Study the following Object-Oriented Programming concepts:
  - **Encapsulation:** Private fields with getters and setters.
  - **ArrayList:** Store transaction history.
  - **Switch-case:** Implement the ATM menu system.
- Refer to GeeksForGeeks or Javatpoint for Java OOP concepts.

---

**Project:** Java Development Internship  
**Task:** Task 3 – ATM Interface  
**Language:** Java
