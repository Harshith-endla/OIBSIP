import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

public class Atm {

    private final Scanner scanner;
    private final Account account;
    private final Bank bank;

    public Atm(Scanner scanner, Account account, Bank bank) {

        if (scanner == null || account == null || bank == null) {
            throw new IllegalArgumentException("Invalid ATM configuration.");
        }

        this.scanner = scanner;
        this.account = account;
        this.bank = bank;
    }

    public void displayMenu() {

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("           ATM MENU");
            System.out.println("================================");
            System.out.println("Welcome, " + account.getAccountId());
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.println("================================");

            System.out.print("Enter your choice: ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    // TransactionHistory displays its own heading.
                    account.getTransactionHistory().displayHistory();
                    break;

                case "2": {
                    BigDecimal previousBalance = account.getBalance();

                    Withdraw withdraw = new Withdraw(
                            scanner,
                            previousBalance,
                            account.getTransactionHistory(),
                            updatedBalance -> {
                                account.setBalance(updatedBalance);
                                saveBankSafely();
                            });

                    BigDecimal updatedBalance = withdraw.startWithdrawal();
                    account.setBalance(updatedBalance);
                    break;
                }

                case "3": {
                    BigDecimal previousBalance = account.getBalance();

                    Deposit deposit = new Deposit(
                            scanner,
                            previousBalance,
                            account.getTransactionHistory());

                    BigDecimal updatedBalance = deposit.startDeposit();
                    account.setBalance(updatedBalance);

                    if (updatedBalance.compareTo(previousBalance) != 0) {
                        saveBankSafely();
                    }
                    break;
                }

                case "4": {
                    Transfer transfer = new Transfer(
                            scanner,
                            account,
                            bank);

                    if (transfer.startTransfer()) {
                        saveBankSafely();
                    }
                    break;
                }

                case "5":
                    // Save once more when leaving as a final safeguard.
                    saveBankSafely();

                    System.out.println("\nThank you for using our ATM.");
                    System.out.println("Have a nice day!");

                    running = false;
                    break;

                default:
                    System.out.println(
                            "\nInvalid choice. Please select 1 to 5.");
            }
        }

        // Save any completed operation if input ended unexpectedly.
        if (running) {
            saveBankSafely();
        }
    }

    // Reports save failures clearly; it never silently claims the data was saved.
    private void saveBankSafely() {

        try {
            bank.saveBank();
            System.out.println("\nAccount data saved successfully.");

        } catch (IOException e) {
            System.out.println(
                    "\nWARNING: The transaction may be complete, but account data could not be saved.");
            System.out.println(
                    "Do not assume this transaction will be available after restarting the ATM.");
            System.out.println("Save error: " + e.getMessage());
        }
    }
}
