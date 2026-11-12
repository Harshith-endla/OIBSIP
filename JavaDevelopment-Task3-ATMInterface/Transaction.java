import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    // Types of ATM transactions
    public enum TransactionType {
        WITHDRAWAL,
        DEPOSIT,
        TRANSFER
    }

    // Centralized transaction descriptions
    public static final class Descriptions {

        public static final String WITHDRAWAL = "Cash Withdrawal";
        public static final String DEPOSIT = "Cash Deposit";

        private Descriptions() {
            // Prevent object creation
        }
    }

    // Encapsulated transaction details
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime dateTime;
    private final String details;

    // Constructor
    public Transaction(
            TransactionType type,
            BigDecimal amount,
            String details) {

        if (type == null || amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Invalid transaction details.");
        }

        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.details = details == null ? "" : details.trim();
    }

    // Returns transaction type
    public TransactionType getType() {
        return type;
    }

    // Returns transaction amount
    public BigDecimal getAmount() {
        return amount;
    }

    // Returns transaction date and time
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    // Returns additional transaction details
    public String getDetails() {
        return details;
    }
}
