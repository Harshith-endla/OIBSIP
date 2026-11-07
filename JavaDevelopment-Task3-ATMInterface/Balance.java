
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;

public class Balance {

    // Encapsulated instance variables
    private final Scanner scanner;
    private final BigDecimal balance;

    // Constructor
    public Balance(Scanner scanner, BigDecimal currentBalance) {

        if (scanner == null || currentBalance == null
                || currentBalance.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException("Invalid balance details.");
        }

        this.scanner = scanner;
        this.balance = currentBalance;
    }

    // Display balance enquiry page
    public void displayBalance() {

        System.out.println("\n================================");
        System.out.println("         BALANCE ENQUIRY");
        System.out.println("================================");

        System.out.println("Available Balance: Rs. " + formatAmount(balance));

        System.out.println("================================");
        System.out.println("1. Return to ATM Menu");
        System.out.println("================================");

        while (true) {

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {

                System.out.println("\nReturning to ATM Menu...");
                return;

            } else {

                System.out.println("\nInvalid option. Please enter 1.");
            }
        }
    }

    // Format balance for display
    private String formatAmount(BigDecimal amount) {

        return String.format(Locale.US, "%,.2f", amount);
    }

    public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);

    BigDecimal testBalance = new BigDecimal("50000");

    Balance balance = new Balance(scanner, testBalance);

    balance.displayBalance();

    scanner.close();
}
}
