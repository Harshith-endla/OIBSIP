TASK 3 · ATM Interface

Objective: Build a console-based simulation of an ATM machine that allows users to authenticate with a PIN and perform standard banking transactions.

Tech Stack: Java (console application, Object-Oriented design with multiple classes)

Feature Checklist:

- [ ] Startup prompt for User ID and PIN; deny access after 3 incorrect attempts
- [ ] Main menu displayed after successful login with the following options:
 - [ ] 1. Transaction History — display a log of all past transactions in the current session
 - [ ] 2. Withdraw — prompt for amount; validate sufficient balance; update balance; log transaction
 - [ ] 3. Deposit — prompt for amount; update balance; log transaction
 - [ ] 4. Transfer — prompt for recipient account ID and amount; validate balance; update both accounts; log transaction
 - [ ] 5. Quit — display a goodbye message and exit
- [ ] Balance check before any withdrawal or transfer; display "Insufficient Funds" if balance is too low
- [ ] All transactions stored in an ArrayList and displayed clearly in Transaction History
- [ ] At least 5 distinct Java classes: ATM, Account, Transaction, Bank, Main

Self-Sourcing Guideline: Search "Java ATM machine project OOP tutorial" on YouTube. This project is a classic OOP exercise — study how to use encapsulation (private fields + getters/setters), ArrayList for history, and switch-case for the menu system. Reference GeeksForGeeks or Javatpoint for Java OOP concepts.
