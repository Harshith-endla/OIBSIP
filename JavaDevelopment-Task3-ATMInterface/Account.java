
import java.math.BigDecimal;

public class Account {

    private final String accountId;
    private BigDecimal balance;
    private final TransactionHistory transactionHistory;

    public Account(String accountId, BigDecimal openingBalance) {

        if (accountId == null || accountId.trim().isEmpty()
                || openingBalance == null
                || openingBalance.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Invalid account details.");
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

        if (balance == null
                || balance.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Invalid account balance.");
        }

        this.balance = balance;
    }

    public void withdrawFunds(BigDecimal amount) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0
                || amount.compareTo(balance) > 0) {

            throw new IllegalArgumentException(
                    "Invalid withdrawal amount.");
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
