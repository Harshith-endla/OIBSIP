
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

public class Deposit {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("100");

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000");

    private static final BigDecimal DENOMINATION = new BigDecimal("100");

    private final Scanner scanner;
    private BigDecimal balance;

    // Constructor
    public Deposit(Scanner scanner, BigDecimal currentBalance) {

        if (scanner == null || currentBalance == null
                || currentBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Invalid deposit details.");
        }

        this.scanner = scanner;
        this.balance = currentBalance;
    }

    // Main deposit operation
    public BigDecimal startDeposit() {

        while (true) {

            System.out.println("\n================================");
            System.out.println("          CASH DEPOSIT");
            System.out.println("================================");
            System.out.println("Available Balance: Rs "
                    + formatAmount(balance));

            BigDecimal amount = getDepositAmount();

            if (amount == null) {
                return balance;
            }

            boolean confirmed = confirmDeposit(amount);

            if (!confirmed) {
                System.out.println("\nDeposit cancelled.");
                return balance;
            }

            BigDecimal previousBalance = balance;

            balance = balance.add(amount);

            displayReceipt(previousBalance, amount);

            while (true) {

                System.out.println("\nWould you like to deposit again?");
                System.out.println("1. Yes, deposit again");
                System.out.println("2. No, return to ATM menu");
                System.out.print("Enter your choice: ");

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
        }
    }

    // Validate deposit amount
    private BigDecimal getDepositAmount() {

        while (true) {

            System.out.print("\nEnter deposit amount (Rs): ");

            String input = scanner.nextLine().trim();

            if (!input.matches("[0-9]+")) {
                System.out.println(
                        "Invalid amount. Enter whole numbers only.");
                continue;
            }

            BigDecimal amount = new BigDecimal(input);

            if (amount.compareTo(MIN_AMOUNT) < 0) {

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

    // Confirm deposit
    private boolean confirmDeposit(BigDecimal amount) {

        while (true) {

            System.out.print(
                    "\nConfirm deposit of Rs "
                            + formatAmount(amount) + "? (Y/N): ");

            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("Y")) {
                return true;
            } else if (confirmation.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.out.println(
                        "Invalid confirmation. Please enter Y or N.");
            }
        }
    }

    // Generate deposit receipt
    private void displayReceipt(
            BigDecimal previousBalance,
            BigDecimal amount) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd-MM-yyyy HH:mm:ss");

        System.out.println("\n================================");
        System.out.println("         DEPOSIT RECEIPT");
        System.out.println("================================");
        System.out.println("Transaction      : Cash Deposit");
        System.out.println("Previous Balance : Rs "
                + formatAmount(previousBalance));
        System.out.println("Deposit Amount   : Rs "
                + formatAmount(amount));
        System.out.println("New Balance      : Rs "
                + formatAmount(balance));
        System.out.println("Date & Time      : "
                + LocalDateTime.now().format(formatter));
        System.out.println("================================");
        System.out.println("       DEPOSIT SUCCESSFUL");
        System.out.println("================================");
    }

    // Format monetary values for display
    private String formatAmount(BigDecimal amount) {

        return String.format(Locale.US, "%,.2f", amount);
    }

}
