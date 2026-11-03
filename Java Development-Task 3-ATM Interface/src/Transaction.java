import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Transaction {

    private final String transactionId;
    private final LocalDateTime timestamp;
    private final BigDecimal amount;
    private final String description;

    public static void main(String[] args) {
        Transaction transaction = new Transaction(
                "TXN1001",
                LocalDateTime.now(),
                new BigDecimal("500.00"),
                "Sample deposit") {
            @Override
            public String getType() {
                return "Deposit";
            }
        };

        System.out.println(transaction.getSummary());
    }

    protected Transaction(
            String transactionId,
            LocalDateTime timestamp,
            BigDecimal amount,
            String description) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException(
                    "Transaction ID cannot be empty.");
        }

        if (timestamp == null) {
            throw new IllegalArgumentException(
                    "Transaction timestamp cannot be null.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Transaction amount cannot be negative or null.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Transaction description cannot be empty.");
        }

        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public abstract String getType();

    public String getSummary() {
        return getType()
                + " | ID: " + transactionId
                + " | Date: " + timestamp
                + " | Amount: " + amount
                + " | " + description;
    }
}

