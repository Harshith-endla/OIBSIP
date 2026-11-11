
import java.math.BigDecimal;
import java.util.Scanner;

public class Deposit {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("100");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000");
    private static final BigDecimal DENOMINATION = new BigDecimal("100");

    private final Scanner scanner;
    private final TransactionHistory transactionHistory;
    private BigDecimal balance;

    public Deposit(
            Scanner scanner,
            BigDecimal currentBalance,
            TransactionHistory transactionHistory) {

        if (scanner == null || currentBalance == null
                || currentBalance.compareTo(BigDecimal.ZERO) < 0
                || transactionHistory == null) {
            throw new IllegalArgumentException(
                    "Invalid deposit details.");
        }

        this.scanner = scanner;
        this.balance = currentBalance;
        this.transactionHistory = transactionHistory;
    }

    public BigDecimal startDeposit() {

        while (true) {
            System.out.println("\n================================");
            System.out.println("           CASH DEPOSIT");
            System.out.println("================================");
            System.out.println("Available Balance: Rs " + balance);

            BigDecimal amount = getDepositAmount();

            if (amount == null) {
                return balance;
            }

            while (true) {
                System.out.print(
                        "\nConfirm deposit of Rs " + amount + "? (Y/N): ");

                if (!scanner.hasNextLine()) {
                    return balance;
                }

                String confirmation = scanner.nextLine().trim();

                if (confirmation.equalsIgnoreCase("Y")) {

                    balance = balance.add(amount);

                    Transaction transaction = new Transaction(
                            Transaction.TransactionType.DEPOSIT,
                            amount,
                            "Cash Deposit");

                    transactionHistory.addTransaction(transaction);

                    displayReceipt(amount, balance);

                    return balance;

                } else if (confirmation.equalsIgnoreCase("N")) {
                    System.out.println("\nDeposit cancelled.");
                    return balance;

                } else {
                    System.out.println(
                            "Invalid confirmation. Please enter Y or N.");
                }
            }
        }
    }

    private BigDecimal getDepositAmount() {

        while (true) {
            System.out.print("\nEnter deposit amount (Rs): ");

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
                System.out.println("Amount must be greater than zero.");

            } else if (amount.compareTo(MIN_AMOUNT) < 0) {
                System.out.println(
                        "Minimum deposit amount is Rs 100.");

            } else if (amount.compareTo(MAX_AMOUNT) > 0) {
                System.out.println(
                        "Maximum deposit amount is Rs 50,000.");

            } else if (amount.remainder(DENOMINATION)
                    .compareTo(BigDecimal.ZERO) != 0) {
                System.out.println(
                        "Deposit amount must be in multiples of Rs 100.");

            } else {
                return amount;
            }
        }
    }

    private void displayReceipt(
            BigDecimal amount,
            BigDecimal updatedBalance) {

        System.out.println("\n================================");
        System.out.println("         DEPOSIT RECEIPT");
        System.out.println("================================");
        System.out.println("Transaction : Cash Deposit");
        System.out.println("Amount      : Rs " + amount);
        System.out.println("New Balance : Rs " + updatedBalance);
        System.out.println("================================");
        System.out.println("Deposit successful!");
        System.out.println("Thank you!");
    }
}
