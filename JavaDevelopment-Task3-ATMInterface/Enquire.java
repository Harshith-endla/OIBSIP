
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;

public class Enquire {

        // Transaction limits
        private static final BigDecimal WITHDRAWAL_LIMIT = new BigDecimal("25000");

        private static final BigDecimal DEPOSIT_LIMIT = new BigDecimal("50000");

        // Encapsulated instance variables
        private final Scanner scanner;
        private final String userId;
        private final BigDecimal balance;

        // Constructor
        public Enquire(Scanner scanner, String userId,
                        BigDecimal currentBalance) {

                if (scanner == null || userId == null || userId.trim().isEmpty()
                                || currentBalance == null
                                || currentBalance.compareTo(BigDecimal.ZERO) < 0) {

                        throw new IllegalArgumentException(
                                        "Invalid account enquiry details.");
                }

                this.scanner = scanner;
                this.userId = userId.trim().toUpperCase(Locale.ROOT);
                this.balance = currentBalance;
        }

        // Display complete account overview
        public void displayEnquiry() {

                System.out.println("\n================================");
                System.out.println("         ACCOUNT ENQUIRY");
                System.out.println("================================");

                System.out.println("Account Holder ID : " + userId);
                System.out.println("Account Type      : Demo Account");
                System.out.println("Account Status    : ACTIVE");

                System.out.println("--------------------------------");
                System.out.println("Available Balance : Rs. "
                                + formatAmount(balance));

                System.out.println("--------------------------------");
                System.out.println("TRANSACTION LIMITS");
                System.out.println("--------------------------------");

                System.out.println("Withdrawal Limit  : Rs. "
                                + formatAmount(WITHDRAWAL_LIMIT));

                System.out.println("Deposit Limit     : Rs. "
                                + formatAmount(DEPOSIT_LIMIT));

                System.out.println("================================");

                System.out.println("\nPress Enter to return to ATM Menu...");
                scanner.nextLine();

                System.out.println("\nReturning to ATM Menu...");
        }

        // Format monetary values
        private String formatAmount(BigDecimal amount) {

                return String.format(Locale.US, "%,.2f", amount);
        }

}
