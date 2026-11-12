import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Withdraw {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("100");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("25000");
    private static final BigDecimal DENOMINATION = new BigDecimal("100");

    private final Scanner scanner;
    private final TransactionHistory transactionHistory;
    private BigDecimal balance;

    // Constructor
    public Withdraw(
            Scanner scanner,
            BigDecimal currentBalance,
            TransactionHistory transactionHistory) {

        if (scanner == null || currentBalance == null
                || currentBalance.compareTo(BigDecimal.ZERO) < 0
                || transactionHistory == null) {

            throw new IllegalArgumentException(
                    "Invalid withdrawal details.");
        }

        this.scanner = scanner;
        this.balance = currentBalance;
        this.transactionHistory = transactionHistory;
    }

    // Starts the withdrawal process
    public BigDecimal startWithdrawal() {

        while (true) {

            System.out.println("\n================================");
            System.out.println("          CASH WITHDRAWAL");
            System.out.println("================================");
            System.out.println("Available Balance: Rs " + balance);

            BigDecimal amount = getWithdrawalAmount();

            if (amount == null) {
                return balance;
            }

            while (true) {

                System.out.print(
                        "\nConfirm withdrawal of Rs " + amount + "? (Y/N): ");

                if (!scanner.hasNextLine()) {
                    return balance;
                }

                String confirmation = scanner.nextLine().trim();

                if (confirmation.equalsIgnoreCase("Y")) {

                    // Deduct the withdrawal amount
                    balance = balance.subtract(amount);

                    // Create and record the transaction
                    Transaction transaction = new Transaction(
                            Transaction.TransactionType.WITHDRAWAL,
                            amount,
                            Transaction.Descriptions.WITHDRAWAL);

                    transactionHistory.addTransaction(transaction);

                    // Display withdrawal receipt
                    displayReceipt(amount, transaction.getDateTime());

                    while (true) {

                        System.out.println(
                                "\nWould you like to withdraw again?");
                        System.out.println("1. Yes, withdraw again");
                        System.out.println("2. No, return to ATM menu");
                        System.out.print("Enter your choice: ");

                        if (!scanner.hasNextLine()) {
                            return balance;
                        }

                        String choice = scanner.nextLine().trim();

                        if (choice.equals("1")) {
                            break;

                        } else if (choice.equals("2")) {
                            return balance;

                        } else {
                            System.out.println(
                                    "Invalid choice. Please select 1 or 2.");
                        }
                    }

                    break;

                } else if (confirmation.equalsIgnoreCase("N")) {

                    System.out.println("\nWithdrawal cancelled.");
                    return balance;

                } else {

                    System.out.println(
                            "Invalid confirmation. Please enter Y or N.");
                }
            }
        }
    }

    // Validates the withdrawal amount
    private BigDecimal getWithdrawalAmount() {

        while (true) {

            System.out.print("\nEnter withdrawal amount (Rs): ");

            if (!scanner.hasNextLine()) {
                return null;
            }

            String input = scanner.nextLine().trim();

            BigDecimal amount;

            try {
                amount = new BigDecimal(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid amount. Please enter a numeric value.");
                continue;
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                System.out.println(
                        "Amount must be greater than zero.");

            } else if (amount.compareTo(MIN_AMOUNT) < 0) {

                System.out.println(
                        "Minimum withdrawal amount is Rs "
                                + MIN_AMOUNT.toPlainString() + ".");

            } else if (amount.compareTo(balance) > 0) {

                System.out.println("Insufficient Funds");
                System.out.println("Available balance: Rs " + balance);

            } else if (amount.compareTo(MAX_AMOUNT) > 0) {

                System.out.println(
                        "Maximum withdrawal amount is Rs "
                                + MAX_AMOUNT.toPlainString() + ".");

            } else if (amount.remainder(DENOMINATION)
                    .compareTo(BigDecimal.ZERO) != 0) {

                System.out.println(
                        "Withdrawal amount must be in multiples of Rs "
                                + DENOMINATION.toPlainString() + ".");

            } else {

                return amount;
            }
        }
    }

    // Displays the withdrawal receipt
    private void displayReceipt(
            BigDecimal amount,
            LocalDateTime transactionDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        System.out.println("\n================================");
        System.out.println("       WITHDRAWAL RECEIPT");
        System.out.println("================================");
        System.out.println("Transaction : "
                + Transaction.Descriptions.WITHDRAWAL);
        System.out.println("Amount      : Rs " + amount);
        System.out.println("Date & Time : "
                + transactionDateTime.format(formatter));
        System.out.println("Remaining   : Rs " + balance);
        System.out.println("================================");
        System.out.println("Please collect your cash.");
        System.out.println("Thank you!");
    }
}
