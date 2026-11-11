import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionHistory {

    // Stores all ATM transactions.
    private final ArrayList<Transaction> transactions = new ArrayList<>();

    // Date and time display format.
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

    // Adds a new transaction to the history.
    public void addTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null.");
        }

        transactions.add(transaction);
    }

    // Returns a read-only copy of transaction history.
    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    // Displays all recorded transactions.
    public void displayHistory() {

        System.out.println("\n================================");
        System.out.println("       TRANSACTION HISTORY");
        System.out.println("================================");

        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        NumberFormat amountFormat = NumberFormat.getNumberInstance(Locale.US);

        amountFormat.setMinimumFractionDigits(2);
        amountFormat.setMaximumFractionDigits(2);

        for (int i = 0; i < transactions.size(); i++) {

            Transaction transaction = transactions.get(i);

            System.out.println("\nTransaction " + (i + 1));
            System.out.println("--------------------------------");
            System.out.println("Type: " + transaction.getType());

            System.out.println("Amount: RS"
                    + amountFormat.format(transaction.getAmount()));

            System.out.println("Date: "
                    + transaction.getDateTime().format(DATE_FORMAT));

            if (!transaction.getDetails().isEmpty()) {
                System.out.println("Details: "
                        + transaction.getDetails());
            }
        }

        System.out.println("\n================================");
    }
}
