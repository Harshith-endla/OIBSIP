
import java.math.BigDecimal;

public class Account {

    private final String accountId;
    private BigDecimal balance;
    private final TransactionHistory transactionHistory;

    public Account(String accountId, BigDecimal openingBalance) {

        // Validate account ID.
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid account ID.");
        }

        // Validate opening balance.
        if (!isValidBalance(openingBalance)) {
            throw new IllegalArgumentException(
                    "Invalid opening balance.");
        }

        this.accountId = accountId.trim().toUpperCase();
        this.balance = openingBalance;
        this.transactionHistory = new TransactionHistory();
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    public void setBalance(BigDecimal balance) {

        if (!isValidBalance(balance)) {
            throw new IllegalArgumentException(
                    "Invalid account balance.");
        }

        this.balance = balance;
    }

    // Reusable balance validation method.
    private boolean isValidBalance(BigDecimal balance) {
        return balance != null
                && balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    public void withdrawFunds(BigDecimal amount) {

        // Validate withdrawal amount.
        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Invalid withdrawal amount.");
        }

        // Check available account balance.
        if (amount.compareTo(balance) > 0) {

            throw new IllegalArgumentException(
                    "Insufficient balance.");
        }

        balance = balance.subtract(amount);
    }

    public void depositFunds(BigDecimal amount) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Invalid deposit amount.");
        }

        balance = balance.add(amount);
    }
}
