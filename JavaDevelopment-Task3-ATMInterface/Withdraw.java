
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Withdraw {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("100");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("25000");
    private static final BigDecimal DENOMINATION = new BigDecimal("100");

    private final Scanner scanner;
    private BigDecimal balance;

    public Withdraw(Scanner scanner, BigDecimal currentBalance) {

        if (scanner == null || currentBalance == null
                || currentBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid withdrawal details.");
        }

        this.scanner = scanner;
        this.balance = currentBalance;
    }

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
                System.out.print("\nConfirm withdrawal of Rs " + amount + "? (Y/N): ");

                if (!scanner.hasNextLine()) {
                    return balance;
                }

                String confirmation = scanner.nextLine().trim();

                if (confirmation.equalsIgnoreCase("Y")) {
                    balance = balance.subtract(amount);
                    displayReceipt(amount);

                    while (true) {
                        System.out.println("\nWould you like to withdraw again?");
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
                            System.out.println("Invalid choice. Please select 1 or 2.");
                        }
                    }

                    break;
                } else if (confirmation.equalsIgnoreCase("N")) {
                    System.out.println("\nWithdrawal cancelled.");
                    return balance;
                } else {
                    System.out.println("Invalid confirmation. Please enter Y or N.");
                }
            }
        }
    }

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
                System.out.println("Invalid amount. Please enter a numeric value.");
                continue;
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount.compareTo(MIN_AMOUNT) < 0) {
                System.out.println("Minimum withdrawal amount is Rs 100.");
            } else if (amount.compareTo(balance) > 0) {
                System.out.println("Insufficient account balance.");
                System.out.println("Available balance: Rs " + balance);
            } else if (amount.compareTo(MAX_AMOUNT) > 0) {
                System.out.println("Maximum withdrawal amount is Rs 25,000.");
            } else if (amount.remainder(DENOMINATION)
                    .compareTo(BigDecimal.ZERO) != 0) {
                System.out.println("Withdrawal amount must be in multiples of Rs 100.");
            } else {
                return amount;
            }
        }
    }

    private void displayReceipt(BigDecimal amount) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        System.out.println("\n================================");
        System.out.println("       WITHDRAWAL RECEIPT");
        System.out.println("================================");
        System.out.println("Transaction : Cash Withdrawal");
        System.out.println("Amount      : Rs " + amount);
        System.out.println("Date & Time : "
                + LocalDateTime.now().format(formatter));
        System.out.println("Remaining   : Rs " + balance);
        System.out.println("================================");
        System.out.println("Please collect your cash.");
        System.out.println("Thank you!");
    }

    public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);

    BigDecimal openingBalance = new BigDecimal("50000");

    Withdraw withdraw = new Withdraw(scanner, openingBalance);

    BigDecimal updatedBalance = withdraw.startWithdrawal();

    System.out.println("\nFinal Account Balance: Rs " + updatedBalance);
}

}
