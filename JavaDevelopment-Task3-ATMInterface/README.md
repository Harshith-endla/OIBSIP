# ATM Interface (Java)

A console-based ATM demonstration application written in Java. It supports user authentication, transaction history, cash withdrawals, cash deposits, and transfers between demo accounts.

## Requirements

- Java Development Kit (JDK) 21 or later
- Windows Command Prompt or PowerShell (the application uses `System.console()` for hidden PIN entry)
- No external libraries or package installation required

## Compile and run

Open Command Prompt in the project folder and run:

```cmd
javac *.java
java Main
```

Run the application from the project folder so the bank data file is created in the expected location.

## Demo accounts

| User ID | PIN | Opening balance |
|---|---|---:|
| USER001 | 1234 | Rs 50,000 |
| USER002 | 5678 | Rs 50,000 |
| USER003 | 9012 | Rs 50,000 |

These credentials are for demonstration only. Do not use real banking credentials.

## Data persistence

- On the first launch, the application creates the initial demo accounts and saves them to `bank-data.ser`.
- After a successful withdrawal, deposit, or transfer, the complete bank data is saved.
- Transfers save both accounts and both transaction histories together.
- Transaction history and balances are restored when the application is restarted.
- If the saved file is unreadable or invalid, the application stops rather than silently replacing it with fresh demo accounts.
- `bank-data.tmp` is used as a temporary file while saving.

**Important:** Keep `bank-data.ser` if you want to preserve account balances and transaction history. Delete it only when you intentionally want to reset the application to the original demo accounts. Back it up before moving or changing the project folder.

## Transaction rules

- Withdrawals: Rs 100 minimum, Rs 25,000 maximum per transaction, in multiples of Rs 100.
- Deposits: Rs 100 minimum, Rs 50,000 maximum per transaction, in multiples of Rs 100.
- Transfers: Rs 100 minimum, Rs 25,000 maximum per transaction, in multiples of Rs 100.
- Transfers cannot be made to the sender's own account.

## Project files

- `Main.java` — application startup and authentication flow
- `Atm.java` — ATM menu and transaction coordination
- `Bank.java` — account collection and local persistence
- `Account.java` — account details and balance operations
- `UserRepository.java` — demo user credentials
- `AuthenticationService.java` — login handling
- `Withdraw.java` — withdrawal workflow
- `Deposit.java` — deposit workflow
- `Transfer.java` — transfer workflow
- `Transaction.java` — transaction data model
- `TransactionHistory.java` — transaction records and display
- `MoneyValidator.java` — shared monetary validation helpers

## Limitations

This is an educational console project, not production banking software. Demo PINs are stored in source code, and Java object serialization is used for local persistence. It does not provide production-grade credential storage, encryption, multi-user concurrency control, or banking-grade transaction guarantees.
